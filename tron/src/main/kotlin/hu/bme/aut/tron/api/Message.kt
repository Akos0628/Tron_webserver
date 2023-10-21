package hu.bme.aut.tron.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class Message

@Serializable
@SerialName("join")
data class JoinMessage(val name: String) : Message() // {"type":"join","name":"akos"}

@Serializable
@SerialName("leave")
data class LeaveMessage(val playerId: String? = null) : Message() // {"type":"leave"}

@Serializable
@SerialName("step")
data class StepMessage(val chat: String) : Message() // {"type":"step","chat":"whoaaa"}