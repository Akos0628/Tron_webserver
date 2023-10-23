package hu.bme.aut.tron.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class ClientMessage

@Serializable
@SerialName("join")
data class JoinMessage(val name: String) : ClientMessage() // {"type":"join","name":"akos"}

@Serializable
@SerialName("leave")
data class LeaveMessage(val playerId: String? = null) : ClientMessage() // {"type":"leave"}

@Serializable
@SerialName("settings")
data class SettingsMessage(val settings: Settings) : ClientMessage()

@Serializable
@SerialName("newMap")
data class NewMapMessage(val msg: String? = null) : ClientMessage()

@Serializable
@SerialName("nextColor")
data class NextColorMessage(val msg: String? = null) : ClientMessage()

@Serializable
@SerialName("ready")
data class ReadyMessage(val value: Boolean) : ClientMessage()

