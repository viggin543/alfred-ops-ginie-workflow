package main.workflow

import com.google.inject.Inject
import main.workflow.alfred.*
import main.workflow.opsGinieApi.Alert
import main.workflow.opsGinieApi.OpsGinieClient
import org.slf4j.LoggerFactory


class Workflow @Inject constructor(private val opsGinieClient: OpsGinieClient) {

    private val log = LoggerFactory.getLogger(App::class.java)!!


    fun listAlerts(): AlfredItems = listFilteredAlerts(listOf())

    fun listFilteredAlerts(args: List<String>): AlfredItems {

        val alfredItems = AlfredItems(
            (opsGinieClient.getAlerts()?.data ?: listOf())
                .filter(matchByMessage(args))
                .map {
                    val alertUrl = "https://app.opsgenie.com/alert/detail/${it.id}/details"
                    AlfredItem(
                        uid = it.tinyId,
                        title = it.message,
                        subtitle = it.alias,
                        arg = alertUrl,
                        autocomplete = it.message,
                        quicklookurl = alertUrl,
                        icon = AlfredIcon(path = "/Users/domrevigor/personal_projects/ops/src/main/resources/opsgenie.jpg"),
                        valid = true,
                        text = AlfredItemText(alertUrl),
                        mods = AlfredMods(AlfredMode(true, "__CLOSE__${it.tinyId}", "close alert"))
                    )
                })
        log.info("listing ${alfredItems.items.size} alerts")
        return alfredItems
    }

    private fun matchByMessage(
        args: List<String>
    ): (Alert) -> Boolean =
        { alert -> args.isEmpty() || alert.message.toLowerCase().contains(args.last().toLowerCase()) }

    fun close(tinyID: String): String {
        log.info("about to close alert with tinyId($tinyID)")
        val resp = opsGinieClient.closeAlert(tinyID)
        log.info("close alert responce: $resp")

        return if (resp == null || resp.result == "Request will be processed")
            "ALERT CLOSED"
        else "FAILED TO CLOSE ALERT"


    }

}