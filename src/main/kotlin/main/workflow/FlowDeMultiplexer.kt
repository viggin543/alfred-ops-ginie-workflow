package main.workflow

import com.google.inject.Inject
import main.workflow.alfred.SimpleAlfredItem
import main.workflow.alfred.SimpleAlfredItems
import org.reflections.Reflections
import java.lang.reflect.Method


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigWorkflow(val commands: Array<String>)

class FlowDeMultiplexer @Inject constructor(
    private val workflow: Workflow,
    private val configurator: WorkFlowConfigurator,
    private val reflections : Reflections
) {

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
            { it.invoke(workflow, args.last().replace(it.getModMagicString(), "")) }
        }
    }
}