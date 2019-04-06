package main.workflow

import com.google.inject.Inject
import org.reflections.Reflections

class WorkFlowConfigurator @Inject constructor(
    private val workflow: Workflow,
    reflections : Reflections) {


    fun isConfigureCommand(args: List<String>) =
        args.isNotEmpty() && commands.contains(args.first())

    fun configure(args: List<String>) =
        workflow.configure(
            listOf(args.first(), args.subList(1, args.size).joinToString(separator = " "))
        )

    private val commands = reflections.getMethodsAnnotatedWith(ConfigWorkflow::class.java).first()
        .getAnnotation(ConfigWorkflow::class.java).commands

}