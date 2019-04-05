package main.workflow

import com.google.inject.Inject
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner

class WorkFlowConfigurator @Inject constructor(private val workflow: Workflow) {


    fun isConfigureCommand(args: List<String>) =
        args.isNotEmpty() && commands.contains(args.first())

    fun configure(args: List<String>) =
        workflow.configure(
            listOf(args.first(), args.subList(1, args.size).joinToString(separator = " "))
        )


    private val reflections = Reflections(
        "main.workflow",
        MethodAnnotationsScanner()
    )

    private val commands = reflections.getMethodsAnnotatedWith(ConfigWorkflow::class.java).first()
        .getAnnotation(ConfigWorkflow::class.java).commands

}