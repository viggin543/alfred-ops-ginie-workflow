package main.workflow.opsGinieApi


import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.http.HttpRequest


@Serializable
data class AlertTeam(val id: String)

@Serializable
data class Alert(
    val seen: Boolean,
    val id: String,
    val tinyId: String,
    val alias: String,
    val message: String,
    val status: String,
    val acknowledged: Boolean,
    val isSeen: Boolean,
    val tags: List<String>,
    val snoozed: Boolean,
    val count: Int,
    val lastOccurredAt: String,
    val createdAt: String,
    val updatedAt: String,
    val source: String,
    val owner: String,
    val priority: String,
    val teams: List<AlertTeam>
)

@Serializable
data class Paging(@Optional val next: String = "", val first: String, val last: String)

@Serializable
data class OpsGinieResponce(
    val data: List<Alert>,
    val paging: Paging,
    val took: Double,
    val requestId: String
)

@Serializable
data class CloseAlertRequestBody(
    val user: String,
    val source: String = "Closed via alfred workflow",
    val note: String = "Action executed via Alert API"
) {

    fun asJsonBody() =
        HttpRequest.BodyPublishers.ofString(
            Json.stringify(
                CloseAlertRequestBody.serializer(),
                this
            )
        )!!

}

@Serializable
data class CloseAlertResponce(val result: String, val took: Double, val requestId: String) {
    fun isAccepted() =
        this.result == "Request will be processed"

}


