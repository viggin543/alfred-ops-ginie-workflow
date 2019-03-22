import kotlinx.serialization.json.Json
import main.AlfredIcon
import main.AlfredItem
import main.AlfredItems
import main.OpsGinieResponce
import main.api.OpsGinieClient
import org.slf4j.LoggerFactory

object App {

    val log = LoggerFactory.getLogger(javaClass.simpleName)

    @JvmStatic
    fun main(args: Array<String>) {
        log.info("this is fun")
        println(Json.stringify(
            AlfredItems.serializer(),
            AlfredItems(
                Json.nonstrict.parse(
                    OpsGinieResponce.serializer(),
                    OpsGinieClient().getAlerts().body()
                ).data.map {
                    AlfredItem(
                        uid = it.tinyId,
                        title = it.message,
                        subtitle = it.alias,
                        arg = it.source,
                        autocomplete = it.message,
                        icon = AlfredIcon(path = "/Users/domrevigor/personal_projects/ops/src/main/resources/opsgenie.jpg"),
                        valid = true
                    )
                })
        ))
    }


}
