import main.workflow.opsGinieApi.OpsGinieClient
import kotlinx.serialization.json.Json
import main.workflow.opsGinieApi.OpsGinieResponce
import org.junit.Test
import kotlin.test.assertNotNull


internal class FunKtTest {


    @Test
    fun `can parse real response`() {

        val jsonResponse =
            OpsGinieClient().getAlerts()

        assertNotNull(jsonResponse)
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