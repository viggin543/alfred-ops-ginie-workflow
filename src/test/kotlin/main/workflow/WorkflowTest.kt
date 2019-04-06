package main.workflow


import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import main.workflow.opsGinieApi.*
import org.mockito.ArgumentMatchers.anyString
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
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
        paging = Paging(next = "next", first = "fst", last = "lst")
    )

    @Test
    fun `can serialize alfred items`() {
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
                )
            )

        val actual = Workflow(opsClient).listFilteredAlerts(listOf()).asJsonString()
        assertNotNull(actual)
    }

    @Test
    fun `can close all alerts like this, 2 matching alerts found `() {
        val opsClient: OpsGinieClient = mock()

        whenever(opsClient.closeAlert(anyString()))
            .thenReturn(
                CloseAlertResponce(result = "Request will be processed", took = 0.1, requestId = "id")
            )

        val alert = Alert(
            false, "", "", "",
            "Unhealthy cluster status in region us-east-1, environment prod - servicedb-11-vc-prod",
            "", false,
            false, listOf(), false, 0, "", "",
            "", "", "", "", listOf()
        )

        whenever(opsClient.getAlerts())
            .thenReturn(
                OpsGinieResponce(
                    listOf(
                        alert,
                        alert.copy(message = "this is a different alert!  Aragorn Loader - At least 1 line failed to parse in adobe loader running in oregon region - monitor-1-vd-prod"),
                        alert.copy(message = "Aragorn Loader - At least 1 line failed to parse in adobe loader running in oregon region - monitor-1-vd-prod"),
                        alert.copy(message = "Aragorn Loader - At least 1 line failed to parse in adobe loader running in miniprod region - monitor-1-vd-prod")
                    ), Paging(first = "", last = ""), 0.1, "id"
                )
            )

        val actual =
            Workflow(opsClient).closeAllLikeThis("Aragorn Loader - At least 1 line failed to parse in adobe loader running in miniprod region - monitor-1-vd-prod")
        assertEquals("2: alerts where closed", actual)
    }

    @Test
    fun `configure must be annotated with three command words`() {
        val reflections = Reflections(
            "main.workflow",
            MethodAnnotationsScanner()
        )

        val actual = reflections.getMethodsAnnotatedWith(ConfigWorkflow::class.java).first()
            .getAnnotation(ConfigWorkflow::class.java).commands
        assertEquals(3, actual.size)
    }


    @Test
    fun `can close all alerts like this, 3 matching alerts found`() {
        val opsClient: OpsGinieClient = mock()

        whenever(opsClient.closeAlert(anyString()))
            .thenReturn(
                CloseAlertResponce(result = "Request will be processed", took = 0.1, requestId = "id")
            )

        val alert = Alert(
            false, "", "", "",
            "Unhealthy cluster status in region us-east-1, environment prod - servicedb-11-vc-prod",
            "", false,
            false, listOf(), false, 0, "", "",
            "", "", "", "", listOf()
        )

        whenever(opsClient.getAlerts())
            .thenReturn(
                OpsGinieResponce(
                    listOf(
                        alert,
                        alert.copy(message = "this is a different alert!  cluster status in region us-east-1, environment prod - servicedb-10-vb-prod"),
                        alert.copy(message = "Unhealthy cluster status in region us-east-1, environment prod - servicedb-11-vx-prod"),
                        alert.copy(message = "Unhealthy cluster status in region us-east-1, environment prod - servicedb-1-vj-prod")
                    ), Paging(first = "", last = ""), 0.1, "id"
                )
            )

        val actual =
            Workflow(opsClient).closeAllLikeThis("Unhealthy cluster status in region us-east-1, environment prod - servicedb-1-vj-prod")
        assertEquals("3: alerts where closed", actual)
    }


    @Test
    fun `can close all alerts like this no matching alerts found`() {
        val opsClient: OpsGinieClient = mock()

        whenever(opsClient.closeAlert("123"))
            .thenReturn(
                CloseAlertResponce(result = "Request will be processed", took = 0.1, requestId = "id")
            )

        val alert = Alert(
            false, "", "", "", "banana - 1", "", false,
            false, listOf(), false, 0, "", "",
            "", "", "", "", listOf()
        )

        whenever(opsClient.getAlerts())
            .thenReturn(
                OpsGinieResponce(
                    listOf(
                        alert,
                        alert.copy(message = "banana - 2"),
                        alert.copy(message = "banana - 3"),
                        alert.copy(message = "banana - 4")
                    ), Paging(first = "", last = ""), 0.1, "id"
                )
            )

        val actual = Workflow(opsClient).closeAllLikeThis("banana")
        assertEquals("0: alerts where closed", actual)


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
                )
            )

        val actual = Workflow(opsClient).listFilteredAlerts(listOf("ddv"))

        assertEquals(actual.items.size, 1)
        assertEquals(actual.items.last().title, "[DDV] HTML CURATOR FAILED")
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
                )
            )

        val actual = Workflow(opsClient).listFilteredAlerts(listOf())

        assertEquals(actual.items.size, 3)
        assertEquals(actual.items.last().arg, "https://app.opsgenie.com/alert/detail/id/details")

    }
}