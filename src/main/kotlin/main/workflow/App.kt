package main.workflow

import com.google.inject.Guice
import kotlinx.serialization.json.Json
import main.workflow.api.OpsGinieClient
import main.workflow.data.AlfredIcon
import main.workflow.data.AlfredItem
import main.workflow.data.AlfredItems
import main.workflow.data.OpsGinieResponce
import org.slf4j.LoggerFactory


object App {
        val log = LoggerFactory.getLogger(App::class.java)
        @JvmStatic
        fun main(args: Array<String>) {
            log.info("starting workflow with args ${args.toList()}")

            val injector = Guice.createInjector(WorkflowModule())

            val opsGinieClient = injector.getInstance(OpsGinieClient::class.java)
            println(
                Json.stringify(
                    AlfredItems.serializer(),
                    AlfredItems(
                        Json.nonstrict.parse(
                            OpsGinieResponce.serializer(),
                            opsGinieClient.getAlerts().body()
                        ).data.map {
                            AlfredItem(
                                uid = it.tinyId,
                                title = it.message,
                                subtitle = it.alias,
                                arg = it.source,
                                autocomplete = it.message,
                                icon = AlfredIcon(path = "/Users/domrevigor/personal_projects/ops/src/main/resources/opsgenie.jpg"),
                                valid = true
                            )
                        })
                )
            )
        }
}
