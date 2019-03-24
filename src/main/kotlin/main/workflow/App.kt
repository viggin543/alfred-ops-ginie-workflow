package main.workflow

import com.google.inject.Guice
import kotlinx.serialization.json.Json
import main.workflow.alfred.AlfredItems
import org.slf4j.LoggerFactory


object App {
    private val log = LoggerFactory.getLogger(App::class.java)!!
    @JvmStatic
    fun main(args: Array<String>) {
        val argsList = args.toList()
        log.info("starting workflow with args $argsList")

        val workflow = Guice.createInjector(WorkflowModule())
            .getInstance(Workflow::class.java)


        when {
            argsList.isEmpty() ->  println(
                Json.stringify(
                    AlfredItems.serializer(),
                    workflow.listAlerts()
                )
            )
            argsList.isNotEmpty() -> println(
                Json.stringify(
                    AlfredItems.serializer(),
                    workflow.listFilteredAlerts(argsList)
                )
            )


        }
    }
}
