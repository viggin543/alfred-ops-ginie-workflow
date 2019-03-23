package main.workflow


import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import main.workflow.opsGinieApi.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WorkflowTest {

    private val alert = Alert(
        seen = false,
        acknowledged = false,
        alias = "alias",
        count = 1,
        createdAt = "createdAt",
        id = "id",
        isSeen = true,
        lastOccurredAt = "",
        message = "[DDV] HTML CURATOR FAILED",
        owner = "me",
        priority = "",
        snoozed = false,
        source = "rtr.",
        status = "stat",
        tags = listOf(""),
        teams = listOf(AlertTeam("ddv")),
        tinyId = "tinyId",
        updatedAt = "tday"
    )
    private val responce = OpsGinieResponce(
        data = listOf(),
        requestId = "opa",
        took = 2.toDouble(),
        paging = Paging(next = "next",first = "fst",last = "lst")
    )



    @Test
    fun `filter alerts by workflow args`() {
        val opsClient: OpsGinieClient = mock()
        val afredItems = listOf(
            alert,
            alert.copy(message = "alert 2"),
            alert.copy(message = "alert 3")
        )
        whenever(opsClient.getAlerts())
            .thenReturn(
                responce.copy(
                    data = afredItems
                ))

        val actual = Workflow(opsClient).run(listOf("da"))

        assertEquals(actual.items.size, 3)

    }
}