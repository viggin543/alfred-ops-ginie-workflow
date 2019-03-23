package main.workflow

import com.google.inject.Guice
import kotlinx.serialization.json.Json
import main.workflow.alfred.AlfredItems
import org.slf4j.LoggerFactory


object App {
    private val log = LoggerFactory.getLogger(App::class.java)!!
    @JvmStatic
    fun main(args: Array<String>) {
        log.info("starting workflow with args ${args.toList()}")

        val workflow = Guice.createInjector(WorkflowModule())
            .getInstance(Workflow::class.java)

        println(
            Json.stringify(
                AlfredItems.serializer(),
                workflow.run(args.toList())
            )
        )
    }
}
