package main.workflow

import com.google.inject.Inject
import main.workflow.alfred.SimpleAlfredItem
import main.workflow.alfred.SimpleAlfredItems
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.slf4j.LoggerFactory


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigWorkflow(val commands: Array<String>)

class FlowDeMultiplexer @Inject constructor(
    private val workflow: Workflow,
    private val configurator: WorkFlowConfigurator
) {

    fun deMultiplex(args: List<String>): String {

        return try {
            when {
                configurator.isConfigureCommand(args) -> configurator.configure(args)

                shouldCloseSingleAlert(args) ->
                    workflow.close(
                        args.last().replace("__CLOSE__", "")
                    )

                shouldCloseAllAlertsLike(args) ->
                    "${workflow.closeAllLikeThis(
                        args.joinToString(separator = " ")
                            .replace("__CLOSE_LIKE_THIS__", "")
                    )}: alerts where closed"

                shouldAckAlert(args) -> workflow.ack(
                    args.joinToString(separator = " ").replace("__ACK_THIS__", "")
                )

                shouldFilterAlerts(args) ->
                    workflow.listFilteredAlerts(args).asJsonString()

                else -> workflow.listAlerts().asJsonString()
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

    private fun shouldAckAlert(argsList: List<String>) =
        !argsList.find { it.contains("__ACK_THIS__") }.isNullOrEmpty()


    private fun shouldCloseAllAlertsLike(argsList: List<String>) =
        !argsList.find { it.contains("__CLOSE_LIKE_THIS__") }.isNullOrEmpty()

    private fun shouldFilterAlerts(argsList: List<String>) = argsList.isNotEmpty()

    private fun shouldCloseSingleAlert(argsList: List<String>) =
        !argsList.find { it.contains("__CLOSE__") }.isNullOrEmpty()
}