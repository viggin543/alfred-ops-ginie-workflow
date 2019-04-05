package main.workflow.opsGinieApi

import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


internal class OpsGinieClientTest {


    @After
    fun tearDown() {
        clean()
    }

    @Before
    fun setUp() {
        clean()
    }

    private fun clean() {
        File("cached_OpsGinieResponce.json").delete()
        File("query").delete()
        File("secret").delete()
        File("user").delete()
    }

    @Test(expected = AssertionError::class)
    fun `throw assert alert on list alerts if query is not configured`() {
        OpsGinieClient(Cache()).getAlerts()
    }

    @Test(expected = AssertionError::class)
    fun `throw assert alert on list alerts if api key is not configured`() {
        File("query").writeText("query")
        OpsGinieClient(Cache()).getAlerts()
    }

    @Test
    fun `can read query from configuration and url encode it`(){
        File("query").writeText("thi sis a query")
        val unit = OpsGinieClient(Cache())
        assertEquals(unit.query,"thi+sis+a+query")

    }

    @Test
    fun `can parse close alert responce body`() {
        val actual = CloseAlertRequestBody(user = "igor").asJsonBody()
        assertEquals(92,actual.contentLength())
    }

    @Test
    fun `can parse api response json`() {
        val jsonResponse = Json.nonstrict.parse(
            OpsGinieResponce.serializer(),
            """{"data":[{
                |"seen": false,
                |"id": "e2bbf8cd-196b-445a-9cf4-c3723b5e09bb-1553009552726",
                |"tinyId": "905",
                |"alias": "monitor-1-vd-prod.inbake.com--dbsync_jobs__StatisticsAggDailyByInternalVideo_table_status_prod",
                |"message": "Prod  - DbSync job for table StatisticsAggDailyByInternalVideo from redshift to analytics is failing - monitor-1-vd-prod",
                |"status": "open",
                |"acknowledged": false,
                |"isSeen": false,
                |"tags": [
                    |"critical",
                    |"monitor",
                    |"production",
                    |"sensu-u16"
                |],
                |"snoozed": false,
                |"count": 1,
                |"lastOccurredAt": "2019-03-19T15:32:32.726Z",
                |"createdAt": "2019-03-19T15:32:32.726Z",
                |"updatedAt": "2019-03-19T19:32:33.394Z",
                |"source": "monitor-1-vd-prod.inbake.com",
                |"owner": "",
                |"priority": "P3",
                |"teams":[{"id":"d113753e-f5d4-41ee-a391-922c3492e016"}],
                |"responders": [
                    |  {
                    |    "type": "team",
                    |    "id": "d113753e-f5d4-41ee-a391-922c3492e016",
                    |    "type": "team"
                    |  }
                    |],
                    |"integration": {
                    |  "id": "dba603dd-fc79-4457-b13a-a4bbd8050ddc",
                    |  "name": "Default API",
                    |  "type": "API"
                    |}
                |}
                |],
            |"paging": {
                |"next": "https://api.opsgenie.com/v2/alerts?limit=10&sort=createdAt&offset=17&order=desc&query=status%3Aopen",
                |"first": "https://api.opsgenie.com/v2/alerts?limit=10&sort=createdAt&offset=0&order=desc&query=status%3Aopen",
                |"last": "https://api.opsgenie.com/v2/alerts?limit=10&sort=createdAt&offset=90&order=desc&query=status%3Aopen"
            },
            |"took":1,"requestId":"da"}""".trimMargin()
        )

        assertNotNull(jsonResponse)
    }

}