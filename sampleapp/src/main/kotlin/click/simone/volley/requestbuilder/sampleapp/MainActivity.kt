package click.simone.volley.requestbuilder.sampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import click.simone.volley.requestbuilder.JSONObjectRequestBuilder
import click.simone.volley.requestbuilder.StringRequestBuilder
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.Volley
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        VolleyLog.DEBUG = true
        val requestQueue = Volley.newRequestQueue(this)
        val r = JSONObjectRequestBuilder()
                .url("http://time.jsontest.com")
                .listener(Response.Listener { response -> println("time fetch: " + response.get("date")) })
                .errorListener(Response.ErrorListener { error -> println("time fetch: " + error) })
                .build()
        requestQueue.add(r)

        val (syncRequest, future) = StringRequestBuilder()
                .url("http://date.jsontest.com")
                .buildBlockingRequest()
        requestQueue.add(syncRequest)
        try {
            val result = future.get(15, TimeUnit.SECONDS)
            println("date fetch: " + result)
        } catch (e: Exception) {
            when (e) {
                is InterruptedException, is ExecutionException, is TimeoutException -> println("date fetch: " + e)
                else -> throw e
            }
        }
    }
}
