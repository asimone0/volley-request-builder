package click.simone.volley.requestbuilder

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class ParserRequestTest{

    val url = "https://test.com"
    val stringParser = StringParser()

    @Test
    fun testUrl(){
        val r = ParserRequest(
            url,
            parser = stringParser,
            listener = Response.Listener { response ->  }
        )
        assertEquals(url, r.url)
    }

    @Test
    fun testParser(){
        val r = ParserRequest(
            url,
            parser = stringParser
        )
        assertEquals(stringParser, r.parser)
    }

    @Test
    fun testMethod(){
        val r = ParserRequest(
            url,
            parser = stringParser,
            method = Request.Method.POST
        )
        assertEquals(Request.Method.POST, r.method)
    }

    @Test
    fun testGETIsDefaultMethod(){
        val r = ParserRequest(
            url,
            parser = stringParser
        )
        assertEquals(Request.Method.GET, r.method)
    }

    @Test
    fun testBody(){
        val body = "testing"
        val r = ParserRequest(
            url,
            parser = stringParser,
            method = Request.Method.POST,
            body = body
        )
        assertArrayEquals(body.toByteArray(), r.body)
    }

    @Test
    fun testHeaders(){
        val h = mutableMapOf("test" to "value")
        val r = ParserRequest(
            url,
            parser = stringParser,
            requestHeaders = h
        )
        assertTrue(h.equals(r.headers));
    }

    @Test
    fun testCacheExpiration(){
        val ce = CacheExpiration(10, TimeUnit.MINUTES)
        val r = ParserRequest(
            url,
            parser = stringParser,
            cacheExpiration = ce
        )
        assertEquals(ce, r.cacheExpiration);
    }

    @Test
    fun testRetryPolicy(){
        val retry = DefaultRetryPolicy(30000, 3, 1f)
        val r = ParserRequest(
            url,
            parser = stringParser,
            policy = retry
        )
        assertEquals(retry.currentTimeout, r.retryPolicy.currentTimeout);
    }

    @Test
    fun testDefaultRetryPolicyIsNotNull(){
        val r = ParserRequest(
            url,
            parser = stringParser
        )
        assertNotNull(r.retryPolicy);
    }

    @Test
    fun testDefaultContentType(){
        val r = ParserRequest(
            url,
            parser = stringParser
        )
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", r.bodyContentType);
    }

    @Test
    fun testPostUsesContentTypeFromHeaders(){
        val r = ParserRequest(
            url,
            method = Request.Method.POST,
            parser = stringParser,
            requestHeaders = mutableMapOf("content-type" to "application/xml"),
            body = "<tag></tag>"
        )
        assertEquals("application/xml", r.bodyContentType);
    }

    @Test
    fun testPutUsesContentTypeFromHeaders(){
        val r = ParserRequest(
            url,
            method = Request.Method.PUT,
            parser = stringParser,
            requestHeaders = mutableMapOf("content-type" to "application/xml"),
            body = "<tag></tag>"
        )
        assertEquals("application/xml", r.bodyContentType);
    }

}