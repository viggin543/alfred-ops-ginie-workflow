package main.workflow

import com.google.inject.Guice
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
            argsList.isEmpty() -> println(
                workflow.listAlerts().asJsonString()
            )
            !argsList.find { it.contains("__CLOSE__") }.isNullOrEmpty() -> println(
                workflow.close(args.last().replace("__CLOSE__", ""))
            )
            argsList.isNotEmpty() -> println(
                workflow.listFilteredAlerts(argsList).asJsonString()
            )

        }
    }
}
