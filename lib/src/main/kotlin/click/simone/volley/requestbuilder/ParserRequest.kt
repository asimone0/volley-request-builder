package click.simone.volley.requestbuilder

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import java.io.IOException
import java.util.*

private const val DEFAULT_TIMEOUT_MILLIS = 5000
private const val CONTENT_TYPE = "content-type"

interface Parser<T>{
    @Throws(IOException::class)
    fun parse(data: ByteArray): T
}

class CustomHeaderRequest<T>(
        url: String,
        protected var listener: Response.Listener<T>? = null,
        errorListener: Response.ErrorListener? = null,
        method: Int = Method.GET,
        protected val body: String? = null,
        additionalHeaders: Map<String, String>? = null,
        val parser: Parser<T>
) : Request<T>(method, url, errorListener) {

    protected val headerMap = mutableMapOf<String, String>()
    protected var contentType: String? = null
    protected val syncLock = Any()

    init {
        initRequest(method, additionalHeaders)
    }

    private fun initRequest(method: Int, additionalHeaders: Map<String, String>?) {
        additionalHeaders?.forEach { entry ->
            try {
                checkForContentType(method, entry.key, entry.value)
            } catch (e: AuthFailureError) {

            }
        }
    }

    @Throws(AuthFailureError::class)
    private fun checkForContentType(method: Int, key: String, value: String) {
        // NOTE: only using PUT and POST methods to capture contentType
        if ((method == Request.Method.PUT || method == Request.Method.POST) && CONTENT_TYPE == key.toLowerCase(Locale.US)) {
            contentType = value
        } else {
            if (CONTENT_TYPE != key.toLowerCase()) {
                headers[key] = value
            }
        }
    }

    /**
     * Super class defaults to application/x-www-form-urlencoded
     * contentType is set from inbound headers in initRequest{}
     */
    override fun getBodyContentType(): String {
        return contentType?.let { it } ?: super.getBodyContentType()
    }

    override fun getBody(): ByteArray? {
        return body?.toByteArray()
    }

    @Throws(AuthFailureError::class)
    override fun getHeaders(): MutableMap<String, String> {
        return headerMap
    }

    override fun cancel() {
        super.cancel()
        synchronized(syncLock) {
            listener = null
        }
    }

    protected override fun deliverResponse(response: T) {
        var tempListener: Response.Listener<T>? = null
        synchronized(syncLock) {
            tempListener = listener
        }
        tempListener?.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<T> {
        try {
            val parsed = parser.parse(response.data)
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e:IOException){
            return Response.error(VolleyError(e))
        }
    }
}