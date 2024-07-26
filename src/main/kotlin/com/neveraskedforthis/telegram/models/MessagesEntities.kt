package com.neveraskedforthis.telegram.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BaseEntity {
    abstract val id: Long
    abstract val date_unixtime: Long
}

@Serializable
@SerialName("message")
data class Message(
    override val id: Long,
    override val date_unixtime: Long,
    val from: String,
    val from_id: String,
    val text_entities: List<TextEntry>
) : BaseEntity()

@Serializable
@SerialName("service")
data class Service(
    override val id: Long,
    override val date_unixtime: Long,
    val actor_id: String,
    val action: String,
) : BaseEntity()

