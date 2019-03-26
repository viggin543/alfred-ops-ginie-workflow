package main.workflow

import com.google.inject.Guice
import org.slf4j.LoggerFactory


object App {
    private val log = LoggerFactory.getLogger(App::class.java)!!
    @JvmStatic
    fun main(args: Array<String>) {
        val argsList = args.toList()
        log.info("starting workflow with args $argsList")

        val flowDeMultiplexer = Guice.createInjector(WorkflowModule())
            .getInstance(FlowDeMultiplexer::class.java)

        println(
            flowDeMultiplexer.deMultiplex(argsList)
        )

    }
}
