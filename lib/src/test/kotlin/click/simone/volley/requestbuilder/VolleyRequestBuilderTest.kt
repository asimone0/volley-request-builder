package click.simone.volley.requestbuilder

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class VolleyRequestBuilderTest {

    val url = "https://test.com"
    val stringParser = StringParser()
    val vrb = VolleyRequestBuilder(url, stringParser)


    @Test
    fun testGetIsDefault() {
        val r = vrb
            .build()
        assertEquals(Request.Method.GET, r.method)
    }

    @Test
    fun testPost() {
        val r = vrb
            .post("{}")
            .build()
        assertEquals(Request.Method.POST, r.method)
    }

    @Test
    fun testPut() {
        val r = vrb
            .put("{}")
            .build()
        assertEquals(Request.Method.PUT, r.method)
    }

    @Test
    fun testDelete() {
        val r = vrb
            .delete()
            .build()
        assertEquals(Request.Method.DELETE, r.method)
    }

    @Test
    fun testHeaders() {
        val h = mutableMapOf("test" to "value")
        val r = vrb
            .headers(h)
            .build()
        assertTrue(r.headers.contains("test"))
        assertEquals("value", r.headers.get("test"))
    }

    @Test
    fun testDefaultContentType() {
        val r = vrb
            .build()
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", r.bodyContentType)
    }

    @Test
    fun testPostDefaultsToJSONContentType() {
        val r = vrb
            .post("{}")
            .build()
        assertEquals("application/json", r.bodyContentType)
    }

    @Test
    fun testPutDefaultsToJSONContentType() {
        val r = vrb
            .put("{}")
            .build()
        assertEquals("application/json", r.bodyContentType)
    }

    @Test
    fun testPostWithHeadersUpdatesContentType() {
        val h = mutableMapOf("content-type" to "application/xml")
        val r = vrb
            .post("<tag></tag>")
            .headers(h)
            .build()
        assertEquals("application/xml", r.bodyContentType)
    }

    @Test
    fun testPutWithHeadersUpdatesContentType() {
        val h = mutableMapOf("content-type" to "application/xml")
        val r = vrb
            .put("<tag></tag>")
            .headers(h)
            .build()
        assertEquals("application/xml", r.bodyContentType)
    }

    @Test
    fun testCacheExpiration(){
        val ce = TimeLapse(10, TimeUnit.MINUTES)
        val r = vrb.cacheExpiration(ce)
            .build()
        assertEquals(ce, (r as? ParserRequest)?.cacheExpiration)
    }

    @Test
    fun testRetryPolicy(){
        val retry = DefaultRetryPolicy(30000, 3, 1f)
        val r = vrb
            .retryPolicy(retry)
            .build()
        assertEquals(retry.currentTimeout, r.retryPolicy.currentTimeout)
    }

    @Test
    fun testDoNotCache(){
        val r = vrb
            .doNotCache()
            .build()
        assertEquals(0L, (r as? ParserRequest)?.cacheExpiration?.millis)
    }

}