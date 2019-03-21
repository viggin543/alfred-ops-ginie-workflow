package main

import kotlinx.serialization.json.Json
import main.api.OpsGinieClient

object App {

    @JvmStatic
    fun main(args: Array<String>) {
        val jsonResponse = Json.nonstrict.parse(
            OpsGinieResponce.serializer(),
            OpsGinieClient().getAlerts().body()
        )
        jsonResponse.data.map { println(it.message) }
    }


}
