package main.workflow

import com.google.inject.Inject
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import viggin543.alfred.workflow.SimpleAlfredItem
import viggin543.alfred.workflow.SimpleAlfredItems
import java.lang.reflect.Method


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigWorkflow(val commands: Array<String>)

class FlowDeMultiplexer @Inject constructor(
    private val workflow: Workflow,
    private val configurator: WorkFlowConfigurator,
    private val reflections : Reflections
) {

    private val log = LoggerFactory.getLogger(App::class.java)!!


    fun deMultiplex(args: List<String>): String {

        val modCommand = alfredModCommand(args)

        return try {
            when {
                configurator.isConfigureCommand(args) -> configurator.configure(args)
                modCommand.isNotEmpty() -> modCommand.first().invoke().toString()
                else -> workflow.listFilteredAlerts(args).asJsonString()
            }
        } catch (e: AssertionError) {
            SimpleAlfredItems(
                listOf(
                    SimpleAlfredItem(
                        title = e.message ?: "plz finish configuration",
                        subtitle = "plz finish configuration"
                    )
                )
            ).asJsonString()
        }
    }


    private fun alfredModCommand(args: List<String>): List<() -> Any> {
        fun Method.getModMagicString() = this.getAnnotation(AlfredMod::class.java).command
        return reflections.getMethodsAnnotatedWith(AlfredMod::class.java).filter { method ->
            args.find { it.contains(method.getModMagicString()) }?.isNotEmpty() ?: false
        }.map {
            { it.invoke(workflow, extractArgs(args, it.getModMagicString())) }
        }
    }

    fun extractArgs(args: List<String>, methodAnnotation: String): String {
        val argument = args.reduce { x, y -> "$x $y" }.removePrefix(methodAnnotation)
        log.info("workflow argument: $argument, made from $args")
        return argument
    }
}