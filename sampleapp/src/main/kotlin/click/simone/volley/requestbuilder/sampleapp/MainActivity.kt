package click.simone.volley.requestbuilder.sampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import click.simone.volley.requestbuilder.CacheExpiration
import click.simone.volley.requestbuilder.JSONObjectRequestBuilder
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        VolleyLog.DEBUG = true
        val requestQueue = Volley.newRequestQueue(this)
        val r = JSONObjectRequestBuilder()
                .url("https://jsonplaceholder.typicode.com/posts/1")
                .listener(Response.Listener { response -> content.text = response.toString() })
                .errorListener(Response.ErrorListener { error -> content.text = error.message })
                .cacheExpirarion(CacheExpiration(1, TimeUnit.MINUTES))
                .build()
        requestQueue.add(r)

    }
}
