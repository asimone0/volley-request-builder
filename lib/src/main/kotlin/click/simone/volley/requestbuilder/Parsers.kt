package click.simone.volley.requestbuilder

import org.json.JSONObject
import java.io.IOException

class StringResponseRequestBuilder(url: String) : VolleyRequestBuilder<String>(url, StringParser())

open class StringParser : Parser<String> {
    override fun parse(data: ByteArray): String {
        try {
            return String(data)
        } catch (e: Exception) {
            throw IOException(e)
        }
    }
}

class JSONObjectResponseRequestBuilder(url: String) : VolleyRequestBuilder<JSONObject>(url, JSONObjectParser())

open class JSONObjectParser : Parser<JSONObject> {
    override fun parse(data: ByteArray): JSONObject {
        try {
            return JSONObject(String(data))
        } catch (e: Exception) {
            throw IOException(e)
        }
    }
}