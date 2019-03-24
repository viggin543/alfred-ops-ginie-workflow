package main.workflow.alfred

import kotlinx.serialization.Serializable

@Serializable
data class AlfredIcon(val type: String="", val path: String)

@Serializable
data class  AlfredMode(val valid: Boolean, val arg: String, val subtitle: String)

@Serializable
data class AlfredMods(val alt: AlfredMode, val cmd: AlfredMode, val ctrl: AlfredMode)


@Serializable
data class AlfredItemText(val copy: String)
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
    val text:AlfredItemText
)
@Serializable
data class AlfredItems(val items: List<AlfredItem>)


