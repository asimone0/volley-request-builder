package click.simone.volley.requestbuilder

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

private const val CONTENT_TYPE = "content-type"

interface Parser<T> {
    @Throws(IOException::class)
    fun parse(data: ByteArray): T
}

data class CacheExpiration(val qty: Long, val timeUnit: TimeUnit) {
    val millis = timeUnit.toMillis(qty)
}

class ParserRequest<T>(
    url: String,
    protected var listener: Response.Listener<T>? = null,
    errorListener: Response.ErrorListener? = null,
    method: Int = Method.GET,
    protected val body: String? = null,
    requestHeaders: Map<String, String>? = null,
    val parser: Parser<T>,
    val cacheExpiration: CacheExpiration? = null,
    val policy: RetryPolicy? = null
) : Request<T>(method, url, errorListener) {

    protected val headerMap = mutableMapOf<String, String>()
    protected var contentType: String? = null
    protected val syncLock = Any()

    init {
        initRequest(method, requestHeaders)
    }

    private fun initRequest(method: Int, requestHeaders: Map<String, String>?) {
        requestHeaders?.forEach { entry ->
            try {
                checkForContentType(method, entry.key, entry.value)
            } catch (e: AuthFailureError) {

            }
        }
        policy?.let { retryPolicy = it }
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
     * contentType is set from inbound requestHeaders in initRequest{}
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
            return cacheExpiration?.let {
                Response.success(parsed, parseIgnoreCacheHeaders(response, it))
            } ?: Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: IOException) {
            return Response.error(VolleyError(e))
        }
    }

    /**
     * A kotlin version https://stackoverflow.com/a/16852314 which allows the cahe time to be passed in
     *
     * Extracts a [Cache.Entry] from a [NetworkResponse].
     * Cache-control requestHeaders are ignored. SoftTtl == 3 mins, ttl == 24 hours.
     * @param response The network response to parse requestHeaders from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    fun parseIgnoreCacheHeaders(response: NetworkResponse, cacheExpiration: CacheExpiration): Cache.Entry {
        val now = System.currentTimeMillis()
        val headers = response.headers
        var serverDate: Long = 0
        var serverEtag: String? = null
        val headerValue: String?
        headerValue = headers["Date"]
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue)
        }
        serverEtag = headers["ETag"]
        val hardExpire = cacheExpiration.millis
        val cacheHitButRefreshed = hardExpire
        val cacheExpired = hardExpire
        val softExpire = now + cacheHitButRefreshed
        val ttl = now + cacheExpired
        val entry = Cache.Entry()
        entry.data = response.data
        entry.etag = serverEtag
        entry.softTtl = softExpire
        entry.ttl = ttl
        entry.serverDate = serverDate
        entry.responseHeaders = headers
        return entry
    }
}