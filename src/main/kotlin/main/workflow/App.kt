package main.workflow

import com.google.inject.Guice
import org.slf4j.LoggerFactory


object App {
        private val log = LoggerFactory.getLogger(App::class.java)!!
        @JvmStatic
        fun main(args: Array<String>) {
            log.info("starting workflow with args ${args.toList()}")

            Guice.createInjector(WorkflowModule())
                .getInstance(Workflow::class.java)
                .run(args.toList())
        }
}
