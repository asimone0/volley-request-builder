package click.simone.volley.requestbuilder

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.RequestFuture

open class VolleyRequestBuilder<T> {

    protected var method: Int = Request.Method.GET
    private var sourceUrl: String? = null
    protected val url: String
        get() {
            return sourceUrl?.let { it }
                    ?: throw IllegalStateException("Url cannot be null")
        }
    protected var body: String? = null
    protected var listener: Response.Listener<T>? = null
    protected var errorListener: Response.ErrorListener? = null
    private val defaultHeaders: MutableMap<String, String> by lazy {
        val hdrs = mutableMapOf<String, String>()
        // May add universal headers here, if needed
        hdrs
    }
    private var additionalHeaders: Map<String, String>? = null
    protected var cacheExpiration: CacheExpiration? = null

    protected val headers: Map<String, String>
        get() {
            additionalHeaders?.let { defaultHeaders.putAll(it) }
            return defaultHeaders
        }

    private var sourceParser: Parser<T>? = null
    protected val parser: Parser<T>
        get() {
            return sourceParser?.let { it }
                    ?: throw IllegalStateException("Parser cannot be null")
        }

    fun get(): VolleyRequestBuilder<T> {
        method = Request.Method.GET
        return this
    }

    fun post(): VolleyRequestBuilder<T> {
        method = Request.Method.POST
        return this
    }

    fun put(): VolleyRequestBuilder<T> {
        method = Request.Method.PUT
        return this
    }

    fun delete(): VolleyRequestBuilder<T> {
        method = Request.Method.DELETE
        return this
    }

    fun url(url: String): VolleyRequestBuilder<T> {
        sourceUrl = url
        return this
    }

    fun body(body: String?): VolleyRequestBuilder<T> {
        this.body = body
        return this
    }

    fun body(body: Any?): VolleyRequestBuilder<T> {
        this.body = body?.toString()
        return this
    }

    fun listener(listener: Response.Listener<T>): VolleyRequestBuilder<T> {
        this.listener = listener
        return this
    }

    fun errorListener(errorListener: Response.ErrorListener): VolleyRequestBuilder<T> {
        this.errorListener = errorListener
        return this
    }

    fun parser(parser: Parser<T>): VolleyRequestBuilder<T> {
        this.sourceParser = parser
        return this
    }

    fun cacheExpirarion(cacheExpiration: CacheExpiration): VolleyRequestBuilder<T> {
        this.cacheExpiration = cacheExpiration
        return this
    }

    fun buildBlockingRequest(): Pair<Request<T>, RequestFuture<T>> {
        val requestFuture: RequestFuture<T> = RequestFuture.newFuture()
        listener = requestFuture
        errorListener = requestFuture
        return Pair(build(), requestFuture)
    }

    fun build(): Request<T> {
        return ParserRequest<T>(url, listener, errorListener, method, body, additionalHeaders, parser, cacheExpiration)
    }
}

