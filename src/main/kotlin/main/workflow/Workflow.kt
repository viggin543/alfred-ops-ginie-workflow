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

    fun run(args: List<String>) =
        AlfredItems(opsGinieClient.getAlerts().data.map {
            AlfredItem(
                uid = it.tinyId,
                title = it.message,
                subtitle = it.alias,
                arg = it.source,
                autocomplete = it.message,
                icon = AlfredIcon(path = "/Users/domrevigor/personal_projects/ops/src/main/resources/opsgenie.jpg"),
                valid = true,
                text = AlfredItemText(it.message)
            )
        })

}