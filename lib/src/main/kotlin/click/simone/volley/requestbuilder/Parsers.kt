package click.simone.volley.requestbuilder

import org.json.JSONObject
import java.io.IOException

class StringRequestBuilder: VolleyRequestBuilder<String>(){
    init {
        parser(StringParser())
    }
}

class StringParser: Parser<String> {
    override fun parse(data: ByteArray): String {
        try{
            return String(data)
        } catch (e: Exception){
            throw IOException(e)
        }
    }
}

class JSONObjectRequestBuilder: VolleyRequestBuilder<JSONObject>(){
    init {
        parser(JSONObjectParser())
    }
}

class JSONObjectParser: Parser<JSONObject> {
    override fun parse(data: ByteArray): JSONObject {
        try{
            return JSONObject(String(data))
        } catch (e: Exception){
            throw IOException(e)
        }
    }
}
