package main.workflow


import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import main.workflow.opsGinieApi.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
    fun `can serialize alfred items`(){
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

        val actual = Workflow(opsClient).listFilteredAlerts(listOf()).asJsonString()
        assertNotNull(actual)
    }



    @Test
    fun `can close alert`() {
        val opsClient: OpsGinieClient = mock()
        val tinyId = "tinyId123"
        whenever(opsClient.closeAlert(tinyId))
            .thenReturn(CloseAlertResponce(result = "Request will be processed", took = 0.1, requestId = "id"))

        val actual = Workflow(opsClient).close(tinyId)
        assertEquals("ALERT CLOSED", actual)
    }



    @Test
    fun `fail to  close alert`() {
        val opsClient: OpsGinieClient = mock()
        val tinyId = "tinyId123"
        whenever(opsClient.closeAlert(tinyId))
            .thenReturn(CloseAlertResponce(result = "sababa", took = 0.1, requestId = "id"))

        val actual = Workflow(opsClient).close(tinyId)
        assertEquals("FAILED TO CLOSE ALERT", actual)

    }

    @Test
    fun `filter alerts by workflow args, args list contains filter, match alert message to lowercase user args`() {
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

        val actual = Workflow(opsClient).listFilteredAlerts(listOf("ddv"))

        assertEquals(actual.items.size, 1)
        assertEquals(actual.items.last().title,"[DDV] HTML CURATOR FAILED")
    }


    @Test
    fun `filter alerts by workflow args, empty args list`() {
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

        val actual = Workflow(opsClient).listFilteredAlerts(listOf())

        assertEquals(actual.items.size, 3)
        assertEquals(actual.items.last().arg,"https://app.opsgenie.com/alert/detail/id/details")

    }
}