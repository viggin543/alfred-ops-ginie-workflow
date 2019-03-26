package main.workflow

import com.google.inject.Inject

class FlowDeMultiplexer @Inject constructor(private val workflow: Workflow) {


    fun deMultiplex(args: List<String>): String {
        return when {

            args.contains("__CONFIGURE_QUERY__") ->
                workflow.configure(args.filter { it != "__CONFIGURE_QUERY__" }.joinToString(separator = " "), "./query")

            args.contains("__CONFIGURE_SECRET__") ->
                workflow.configure(args.filter { it != "__CONFIGURE_SECRET__" }, "./secret")

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