package hu.bme.aut.tron.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class ClientMessage

@Serializable
@SerialName("join")
data class JoinMessage(val name: String) : ClientMessage()

@Serializable
@SerialName("leave")
data class LeaveMessage(val playerId: String? = null) : ClientMessage()

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

@Serializable
@SerialName("step")
data class StepMessage(val direction: Direction, val x: Int? = null, val y: Int? = null) : ClientMessage()

@Serializable
@SerialName("inGame")
data class InGameMessage(val inGame: Boolean? = null) : ClientMessage()