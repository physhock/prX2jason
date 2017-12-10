package trpo

import com.google.gson.*
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

fun JsonObject.emplaceProperty(key: String, value: String) = this.also { addProperty(key, value) }
fun JsonObject.emplaceProperty(key: String, value: Int) = this.also { addProperty(key, value) }

val errorMessageRegex = Regex("[^:]+:(.*) at line (\\d+) column (\\d+) .*")

fun findOrDefault(errorMessage: String?, vararg defaults: String): List<String> {
    if (errorMessage == null) return defaults.toList()
    val groups = errorMessageRegex.find(errorMessage) ?: return defaults.toList()
    val destructed = groups.destructured.toList()
    if (destructed.size != defaults.size) return defaults.toList()
    return destructed
}

fun JsonParser.processJson(jsonString: String) = try {
    parse(jsonString)
} catch (ex: JsonParseException) {
    val (message, line, column) = findOrDefault(ex.message, "Unknown error", "-1", "-1")
    JsonObject().emplaceProperty("errorCode", message.hashCode())
            .emplaceProperty("errorMessage", message)
            .emplaceProperty("errorPlace", "line $line column $column")
}

fun main(args: Array<String>) {
    val parser = JsonParser()
    val builder = GsonBuilder().setPrettyPrinting().create()
    val server = HttpServer.create()
    val serverPort = (System.getenv("SERVER_PORT") ?: "7777").toInt()
    server.bind(InetSocketAddress(serverPort), 0)
    server.createContext("/") {
        val responseObject = parser.processJson(it.requestBody.bufferedReader().readText())
        val response = builder.toJson(responseObject) + "\n"
        it.sendResponseHeaders(200, response.length.toLong())
        it.responseBody.write(response.toByteArray())
        it.responseBody.close()
    }
    server.executor = null
    server.start()
}
