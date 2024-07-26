package com.neveraskedforthis.telegram

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val name: String,
    val type: String,
    val id: Long,
    val messages: List<BaseEntity>,
)