package click.simone.volley.requestbuilder

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.RequestFuture
import java.util.concurrent.TimeUnit


open class VolleyRequestBuilder<T> constructor(val url: String, val parser: Parser<T>) {

    protected var method: Int = Request.Method.GET
    protected var body: String? = null
    protected var listener: Response.Listener<T>? = null
    protected var errorListener: Response.ErrorListener? = null

    /**
     * Globally applied, default headers (added to all requests built by this class)
     */
    private val defaultHeaders: MutableMap<String, String> by lazy {
        val hdrs = mutableMapOf<String, String>()
        
        // Default body type for put/post
        hdrs.put("content-type", "application/json")

        // May add other universal headers here, if needed
        hdrs
    }
    /**
     * Additional headers (added to an individual request and will override any defaultHeaders values)
     */
    private var additionalHeaders: Map<String, String>? = null
    protected var cacheExpiration: CacheExpiration? = null
    protected var retryPolicy: RetryPolicy? = null

    /**
     * Headers that represent the net outcome of default additional
     * These are used to build the actual request
     */
    protected val headers: Map<String, String>
        get() {
            additionalHeaders?.let { defaultHeaders.putAll(it) }
            return defaultHeaders
        }

    fun get(): VolleyRequestBuilder<T> {
        method = Request.Method.GET
        return this
    }

    /**
     * Post assumes a JSON body (see defaultHeaders above)
     * If this is not the case, you must set the Content-Type with a call to headers()
     */
    fun post(json: Any?): VolleyRequestBuilder<T> {
        method = Request.Method.POST
        this.body = json?.toString()
        return this
    }

    /**
     * Put assumes a JSON body (see defaultHeaders above)
     * If this is not the case, you must set the Content-Type with a call to headers()
     */
    fun put(json: Any?): VolleyRequestBuilder<T> {
        method = Request.Method.PUT
        this.body = json?.toString()
        return this
    }

    fun delete(): VolleyRequestBuilder<T> {
        method = Request.Method.DELETE
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

    fun errorListener(errorListener: (VolleyError) -> Unit): VolleyRequestBuilder<T> {
        this.errorListener = Response.ErrorListener(errorListener)
        return this
    }

    fun headers(headers: Map<String, String>): VolleyRequestBuilder<T> {
        this.additionalHeaders = headers
        return this
    }

    fun cacheExpiration(cacheExpiration: CacheExpiration): VolleyRequestBuilder<T> {
        this.cacheExpiration = cacheExpiration
        return this
    }

    fun retryPolicy(policy: RetryPolicy): VolleyRequestBuilder<T> {
        retryPolicy = policy
        return this
    }

    fun doNotCache(): VolleyRequestBuilder<T> {
        this.cacheExpiration = CacheExpiration(0, TimeUnit.MILLISECONDS)
        return this
    }

    fun buildBlockingRequest(): Pair<Request<T>, RequestFuture<T>> {
        val requestFuture: RequestFuture<T> = RequestFuture.newFuture()
        listener = requestFuture
        errorListener = requestFuture
        return Pair(build(), requestFuture)
    }

    fun build(): Request<T> {
        return ParserRequest<T>(url, listener, errorListener, method, body, headers, parser, cacheExpiration, retryPolicy)
    }
}
