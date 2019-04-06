package main.workflow.opsGinieApi

import com.google.inject.Inject
import kotlinx.serialization.json.Json
import main.workflow.App
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.Charset

open class OpsGinieClient @Inject constructor(private val cache: Cache) {

    private val log = LoggerFactory.getLogger(App::class.java)!!

    //todo use something better for configuration like pure config or an ini file
    private val apiKey = safeReadConfigValue("./secret")
    val query = URLEncoder.encode(safeReadConfigValue("./query"), Charset.defaultCharset())!!
    private val user = safeReadConfigValue("./user")


    open fun closeAlert(tinyId: String): CloseAlertResponce? {
        assert(apiKey.isNotEmpty()) { "plz configure ops ginie apiKey" }
        assert(user.isNotEmpty()) { "plz configure ops ginie user" }

        return try {
            val resp = opsPostAction(tinyId, "close")
            cache.invalidate()
            resp
        } catch (e: Exception) {
            log.error("closing alert failed with error : ${e.message}")
            null
        }
    }


    open fun ackAlert(tinyId: String): CloseAlertResponce {
        assert(apiKey.isNotEmpty()) { "plz configure ops ginie apiKey" }
        assert(user.isNotEmpty()) { "plz configure ops ginie user" }
        
        return opsPostAction(tinyId, "acknowledge")
    }


    open fun getAlerts(): OpsGinieResponce? {

        assert(query.isNotEmpty()) { "plz configure ops ginie query" }
        assert(apiKey.isNotEmpty()) { "plz configure ops ginie apiKey" }

        return cache.get {
            try {
                log.info("about to call ops ginie api")
                (Json.nonstrict.parse(
                    OpsGinieResponce.serializer(),
                    HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder()
                            .uri(URI.create("https://api.opsgenie.com/v2/alerts?query=$query&limit=200&sort=createdAt&order=desc"))
                            .header("Authorization", "GenieKey $apiKey")
                            .build(), HttpResponse.BodyHandlers.ofString()
                    ).body()
                )
                        )
            } catch (e: Exception) {
                log.error("request failed ${e.message}")
                null
            }
        }
    }

    private fun opsPostAction(tinyId: String, action: String): CloseAlertResponce {
        return Json.nonstrict.parse(
            CloseAlertResponce.serializer(), HttpClient.newHttpClient().send(
                HttpRequest.newBuilder() //todo test me with wiremock plz
                    .uri(URI.create("https://api.opsgenie.com/v2/alerts/$tinyId/$action?identifierType=tiny"))
                    .header("Authorization", "GenieKey $apiKey")
                    .header("Content-Type", "application/json")
                    .POST(CloseAlertRequestBody(user = user).asJsonBody())
                    .build(), HttpResponse.BodyHandlers.ofString()
            ).body()
        )
    }

    private fun safeReadConfigValue(key: String): String {
        return try {
            File(key).readText(Charsets.UTF_8)
        } catch (e: FileNotFoundException) {
            log.error("missing ops ginie $key")
            ""
        }
    }


}