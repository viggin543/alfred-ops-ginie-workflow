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


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AlfredMod(val command: String)


open class Workflow @Inject constructor(private val opsGinieClient: OpsGinieClient) {

    private val log = LoggerFactory.getLogger(App::class.java)!!

    open fun listFilteredAlerts(args: List<String>): AlfredItems {

        fun matchByMessage(
            args: List<String>
        ): (Alert) -> Boolean = { alert ->
            args.isEmpty() || alert.message.toLowerCase().contains(args.last().toLowerCase())
        }


        val alfredItems = AlfredItems(
            (opsGinieClient.getAlerts()?.data ?: listOf())
                .filter(matchByMessage(args))
                .map { it.asAlfredItem() })
        log.info("listing ${alfredItems.items.size} alerts")
        return alfredItems
    }

    @AlfredMod("__CLOSE__")
    open fun close(tinyID: String): String {
        log.info("about to close alert with tinyId($tinyID)")
        val resp = opsGinieClient.closeAlert(tinyID)
        log.info("close alert responce: $resp")

        return if (resp?.isAccepted() == true)
            "ALERT CLOSED"
        else "FAILED TO CLOSE ALERT"
    }

    @AlfredMod("__CLOSE_LIKE_THIS__")
    open fun closeAllLikeThis(message: String): String {
        log.info("about to close alert with message($message)")
        val alertsToClose = listFilteredAlerts(listOf()).items.filter {
            LevenshteinDistance()
                .apply(it.title, message).toDouble() < 8
        }
        return "${when {
            alertsToClose.isNotEmpty() -> alertsToClose.map {
                log.info("about to close ${it.title}, ${it.uid}")
                if (opsGinieClient.closeAlert(it.uid)?.isAccepted() == true) 1 else 0
            }.reduce { acc, result ->
                log.info("alerts closed:  $result")
                acc + result
            }
            else -> 0
        }}: alerts where closed"
    }

    @AlfredMod("__ACK_THIS__")
    open fun ack(tinyId: String): String {
        log.info("acking alert $tinyId")
        val result = opsGinieClient.ackAlert(tinyId).result
        log.info("ack result is $result")
        return result
    }

    @ConfigWorkflow(commands = ["__CONFIGURE_QUERY__", "__CONFIGURE_SECRET__", "__CONFIGURE_USER__"])
    fun configure(args: List<String>): String {
        fun configCommandToFileName(command: String) =
            command.replace("CONFIGURE", "")
                .replace("_", "").toLowerCase()

        val path = configCommandToFileName(args.first())
        log.info("configuring $path: $args")
        Files.write(Paths.get(path), args.last().toUtf8Bytes())
        return "wrote $path to workflow directory"
    }


    private fun Alert.asAlfredItem() = AlfredItem(
        uid = this.tinyId,
        title = this.message,
        subtitle = this.alias,
        arg = "https://app.opsgenie.com/alert/detail/${this.id}/details",
        autocomplete = this.message,
        quicklookurl = "https://app.opsgenie.com/alert/detail/${this.id}/details",
        icon = AlfredIcon(path = "/Users/domrevigor/personal_projects/ops/src/main/resources/opsgenie.jpg"),
        valid = true,
        text = AlfredItemText("https://app.opsgenie.com/alert/detail/${this.id}/details"),
        mods = AlfredMods(
            cmd = AlfredMode(true, "__CLOSE__${this.tinyId}", "close alert"),
            shift = AlfredMode(true, "__CLOSE_LIKE_THIS__${this.message}", "close alerts like this"),
            alt = AlfredMode(true, "__ACK_THIS__${this.tinyId}", "ack this alert")
        )
    )
}

