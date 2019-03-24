package main.workflow

import com.google.inject.Inject
import main.workflow.alfred.AlfredIcon
import main.workflow.alfred.AlfredItem
import main.workflow.alfred.AlfredItemText
import main.workflow.alfred.AlfredItems
import main.workflow.opsGinieApi.OpsGinieClient
import org.slf4j.LoggerFactory


class Workflow @Inject constructor(private val opsGinieClient: OpsGinieClient) {

    private val log = LoggerFactory.getLogger(App::class.java)!!

    fun run(args: List<String>): AlfredItems {

        val alfredItems = AlfredItems(opsGinieClient.getAlerts().data
            .filter { args.isEmpty() || it.message.contains(args.last()) }
            .map {
            val alertUrl = "https://app.opsgenie.com/alert/detail/${it.id}/details"
            log.info("arg is -> $alertUrl")
            AlfredItem(
                uid = it.tinyId,
                title = it.message,
                subtitle = it.alias,
                arg = alertUrl,
                autocomplete = it.message,
                quicklookurl = alertUrl,
                icon = AlfredIcon(path = "/Users/domrevigor/personal_projects/ops/src/main/resources/opsgenie.jpg"),
                valid = true,
                text = AlfredItemText(alertUrl)
            )
        })
        log.info("listing ${alfredItems.items.size} alerts")
        return alfredItems
    }
}