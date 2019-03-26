package main.workflow

import com.google.inject.Inject
import org.slf4j.LoggerFactory

class FlowDeMultiplexer @Inject constructor(private val workflow: Workflow) {

    private val log = LoggerFactory.getLogger(App::class.java)!!


    fun deMultiplex(args: List<String>): String {
        return when {

            shouldCloseSingleAlert(args) ->
                workflow.close(
                    args.last().replace("__CLOSE__", "")
                )

            shouldCloseAllAlertsLike(args) ->
                workflow.closeAllLikeThis(
                    args.joinToString(separator = " ")
                        .replace("__CLOSE_LIKE_THIS__", "")
                )

            shouldFilterAlerts(args) ->
                workflow.listFilteredAlerts(args).asJsonString()

            else -> workflow.listAlerts().asJsonString()
        }
    }


    private fun shouldCloseAllAlertsLike(argsList: List<String>) =
        !argsList.find { it.contains("__CLOSE_LIKE_THIS__") }.isNullOrEmpty()

    private fun shouldFilterAlerts(argsList: List<String>) = argsList.isNotEmpty()

    private fun shouldCloseSingleAlert(argsList: List<String>) =
        !argsList.find { it.contains("__CLOSE__") }.isNullOrEmpty()
}