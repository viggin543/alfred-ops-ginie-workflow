package main.workflow.alfred

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AlfredIcon(val type: String="", val path: String)

@Serializable
data class  AlfredMode(val valid: Boolean, val arg: String, val subtitle: String)

@Serializable
data class AlfredMods(val cmd: AlfredMode)


@Serializable
data class AlfredItemText(val copy: String)



@Serializable
data class SimpleAlfredItem(val uid: String,
                            val title: String,
                            val subtitle: String,
                            val arg: String = title)
@Serializable
data class SimpleAlfredItems(val items: List<SimpleAlfredItem>) {
    fun asJsonString() = Json.stringify(SimpleAlfredItems.serializer(),this)

}


@Serializable
data class AlfredItem(
    val uid: String,
    val title: String,
    val subtitle: String,
    val arg: String,
    val autocomplete: String,
    val icon: AlfredIcon,
    val valid: Boolean,
    val quicklookurl: String,
    val text:AlfredItemText,
    val mods: AlfredMods
)

@Serializable
data class AlfredItems(val items: List<AlfredItem>) {
    fun asJsonString() = Json.stringify(
            AlfredItems.serializer(),
            this
        )

}


