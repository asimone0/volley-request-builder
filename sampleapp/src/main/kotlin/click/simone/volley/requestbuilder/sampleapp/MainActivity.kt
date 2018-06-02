package click.simone.volley.requestbuilder.sampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import click.simone.volley.requestbuilder.CacheExpiration
import click.simone.volley.requestbuilder.JSONObjectRequestBuilder
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.Volley
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        VolleyLog.DEBUG = true
        val requestQueue = Volley.newRequestQueue(this)
        val r = JSONObjectRequestBuilder()
                .url("https://jsonplaceholder.typicode.com/posts/1")
                .listener(Response.Listener { response -> println("JSON fetch: " + response) })
                .errorListener(Response.ErrorListener { error -> println("JSON fetch: " + error) })
                .cacheExpirarion(CacheExpiration(1, TimeUnit.MINUTES))
                .build()
        requestQueue.add(r)

    }
}
