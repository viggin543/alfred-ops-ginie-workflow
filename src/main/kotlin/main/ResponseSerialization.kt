package main

import kotlinx.serialization.Serializable


@Serializable
data class AlertTeam(val id: String)

@Serializable
data class Alert(
    val seen: Boolean,
    val id: String,
    val tinyId: String,
    val alias: String,
    val message: String,
    val status: String,
    val acknowledged: Boolean,
    val isSeen: Boolean,
    val tags: List<String>,
    val snoozed: Boolean,
    val count: Int,
    val lastOccurredAt: String,
    val createdAt: String,
    val updatedAt: String,
    val source: String,
    val owner: String,
    val priority: String,
    val teams: List<AlertTeam>
)

@Serializable
data class Paging(val next: String, val first: String, val last: String)

@Serializable
data class OpsGinieResponce(val data: List<Alert>, val paging: Paging, val took: Double, val requestId: String)

@Serializable
data class AlfredIcon(val type: String="", val path: String)

@Serializable
data class  AlfredMode(val valid: Boolean, val arg: String, val subtitle: String)

@Serializable
data class AlfredMods(val alt: AlfredMode,val cmd: AlfredMode,val ctrl: AlfredMode )

@Serializable
data class AlfredItem(
    val uid: String,
    val title: String,
    val subtitle: String,
    val arg: String,
    val autocomplete: String,
    val icon: AlfredIcon,
    val valid: Boolean
//    val mods: AlfredMods
)
@Serializable
data class AlfredItems(val items: List<AlfredItem>)


