package main.workflow.opsGinieApi

import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.Charset

open class OpsGinieClient {

    private val query = URLEncoder.encode(
        """(status: open AND teams: ("aragorn" OR "aragorn_counting" OR "ddv_render" OR "renderer" OR "ddv_billing" OR "services")) """
        + """OR (status: open AND owner: (ron@innovid.com OR elad@innovid.com OR igor@innovid.com OR gal.berger@innovid.com))"""
        , Charset.defaultCharset())


    open fun getAlerts() =
         Json.nonstrict.parse(OpsGinieResponce.serializer(),
             HttpClient.newHttpClient().send(
            HttpRequest.newBuilder()
                .uri(URI.create("https://api.opsgenie.com/v2/alerts?query=$query&offset=7&limit=20&sort=createdAt&order=desc"))
                .header("Authorization", "GenieKey 719846f6-904c-4fab-b047-f306b3303c65")
                .build(), HttpResponse.BodyHandlers.ofString()
        ).body())

}