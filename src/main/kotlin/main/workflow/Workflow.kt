package main.workflow

import com.google.inject.Inject
import kotlinx.serialization.toUtf8Bytes
import main.workflow.alfred.*
import main.workflow.opsGinieApi.Alert
import main.workflow.opsGinieApi.OpsGinieClient
import org.apache.commons.text.similarity.LevenshteinDistance

import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths



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

        return if (resp?.isAccepted() == true)
            "ALERT CLOSED"
        else "FAILED TO CLOSE ALERT"
    }


    open fun closeAllLikeThis(message: String): Int {
        log.info("about to close alert with message($message)")
        val alertsToClose = listAlerts().items.filter {
            LevenshteinDistance()
                .apply(it.title, message).toDouble() < 8
        }
        return when {
            alertsToClose.isNotEmpty() -> alertsToClose.map {
                log.info("about to close ${it.title}, ${it.uid}")
                if (opsGinieClient.closeAlert(it.uid)?.isAccepted() == true) 1 else 0
            }.reduce { acc, result ->
                log.info("alerts closed:  $result")
                acc + result
            }
            else -> 0
        }
    }

    fun configure(arg: String, path: String) = configure(listOf(arg), path)

    fun configure(args: List<String>, path: String): String {
        log.info("configuring $path: $args")
        Files.write(Paths.get(path), args.last().toUtf8Bytes())
        return "wrote $path to workflow directory"
    }


}