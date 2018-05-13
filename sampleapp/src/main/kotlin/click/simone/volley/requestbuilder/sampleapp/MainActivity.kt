package click.simone.volley.requestbuilder.sampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import click.simone.volley.requestbuilder.JSONObjectParser
import click.simone.volley.requestbuilder.VolleyRequestBuilder
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val r = VolleyRequestBuilder<JSONObject>()
                .url("http://time.jsontest.com")
                .listener(Response.Listener { response ->  println(response.get("date"))})
                .parser(JSONObjectParser())
                .build()
        VolleyLog.DEBUG = true
        Volley.newRequestQueue(this).add(r)
    }
}
