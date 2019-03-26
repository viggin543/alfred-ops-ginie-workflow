package main.workflow

import com.google.inject.Inject
import main.workflow.alfred.*
import main.workflow.opsGinieApi.Alert
import main.workflow.opsGinieApi.OpsGinieClient
import org.slf4j.LoggerFactory


open class Workflow @Inject constructor(private val opsGinieClient: OpsGinieClient) {

    private val log = LoggerFactory.getLogger(App::class.java)!!

    open fun listAlerts(): AlfredItems = listFilteredAlerts(listOf())

    open fun listFilteredAlerts(args: List<String>): AlfredItems {

        fun matchByMessage(
            args: List<String>
        ): (Alert) -> Boolean =
            { alert -> args.isEmpty() || alert.message.toLowerCase().contains(args.last().toLowerCase()) }


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
                        mods = AlfredMods(
                            cmd = AlfredMode(true, "__CLOSE__${it.tinyId}", "close alert"),
                            shift = AlfredMode(true, "__CLOSE_LIKE_THIS__${it.message}", "close alerts like this")
                        )
                    )
                })
        log.info("listing ${alfredItems.items.size} alerts")
        return alfredItems
    }


    open fun close(tinyID: String): String {
        log.info("about to close alert with tinyId($tinyID)")
        val resp = opsGinieClient.closeAlert(tinyID)
        log.info("close alert responce: $resp")

        return if (resp == null || resp.result == "Request will be processed")
            "ALERT CLOSED"
        else "FAILED TO CLOSE ALERT"
    }

    open fun closeAllLikeThis(message: String): String {
        log.info("about to close alert with message($message)")
        return listAlerts().items.filter { it.title == message }.map {
            log.info("about to close ${it.title}, ${it.uid}")
            opsGinieClient.closeAlert(it.uid)?.result ?: "FAILED"
        }.reduce { acc, result ->
            log.info("closing alert responce $result")
            acc + result
        }
    }
}