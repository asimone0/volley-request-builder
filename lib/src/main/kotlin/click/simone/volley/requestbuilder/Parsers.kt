package click.simone.volley.requestbuilder

import org.json.JSONObject
import java.io.IOException


class StringParser: Parser<String> {
    override fun parse(data: ByteArray): String {
        try{
            return String(data)
        } catch (e: Exception){
            throw IOException(e)
        }
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
