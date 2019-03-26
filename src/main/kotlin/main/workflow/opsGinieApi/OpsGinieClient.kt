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


    private val query = try {URLEncoder.encode(
        File("./query").readText(Charsets.UTF_8)
        , Charset.defaultCharset()
    )} catch (e: FileNotFoundException) {
        log.error("missing ops ginie query")
        ""
    }

    private val apiKey = try {
        File("./secret").readText(Charsets.UTF_8)
    } catch (e: FileNotFoundException) {
        log.error("missing ops ginie secret")
        ""
    }

    open fun closeAlert(tinyId: String): CloseAlertResponce? {
        return try {
            val resp = Json.nonstrict.parse(
                CloseAlertResponce.serializer(),
                HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder()
                        .uri(URI.create("https://api.opsgenie.com/v2/alerts/$tinyId/close?identifierType=tiny"))
                        .header("Authorization", "GenieKey $apiKey")
                        .header("Content-Type", "application/json")
                        .POST(CloseAlertRequestBody(user = "igor").asJsonBody())
                        .build(), HttpResponse.BodyHandlers.ofString()
                ).body()
            )
            cache.invalidate()
            resp
        } catch (e: Exception) {
            log.error("closing alert failed with error : ${e.message}")
            null
        }
    }

    open fun getAlerts(): OpsGinieResponce? {

        return cache.get {
            try {
                log.info("about to call ops ginie api")
                (Json.nonstrict.parse(
                    OpsGinieResponce.serializer(),
                    HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder()
                            .uri(URI.create("https://api.opsgenie.com/v2/alerts?query=$query&limit=20&sort=createdAt&order=desc"))
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

}