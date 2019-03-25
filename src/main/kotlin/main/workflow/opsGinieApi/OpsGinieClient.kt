package main.workflow.opsGinieApi

import com.google.inject.Inject
import kotlinx.serialization.json.Json
import main.workflow.App
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.Charset

open class OpsGinieClient @Inject constructor(private val cache: Cache) {

    private val query = URLEncoder.encode(
        """(status: open AND teams: ("aragorn" OR "aragorn_counting" OR "ddv_render" OR "renderer" OR "ddv_billing" OR "services")) """
                + """OR (status: open AND owner: (ron@innovid.com OR elad@innovid.com OR igor@innovid.com OR gal.berger@innovid.com))"""
        , Charset.defaultCharset()
    )

    private val log = LoggerFactory.getLogger(App::class.java)!!

    private val apiKey = "719846f6-904c-4fab-b047-f306b3303c65"

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