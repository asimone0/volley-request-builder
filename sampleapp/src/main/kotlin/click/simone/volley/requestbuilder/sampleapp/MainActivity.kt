package click.simone.volley.requestbuilder.sampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import click.simone.volley.requestbuilder.JSONObjectResponseRequestBuilder
import click.simone.volley.requestbuilder.StringResponseRequestBuilder
import com.android.volley.VolleyLog
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        VolleyLog.DEBUG = true
        val requestQueue = Volley.newRequestQueue(this)
        // asynchronous sample usage
        val r = JSONObjectResponseRequestBuilder("https://jsonplaceholder.typicode.com/posts/1")
            .listener { response -> content.text = response?.toString() ?: "Null response" }
            .errorListener { error -> content.text = error?.message ?: "Unknown error" }
            .cacheExpiration(1, TimeUnit.MINUTES)
            .build()
        requestQueue.add(r)

        GlobalScope.launch {
            // synchronous sample usage
            val result = StringResponseRequestBuilder("https://jsonplaceholder.typicode.com/posts/1/comments")
                    .performBlockingRequest(requestQueue)
            println(result)
        }

    }
}
