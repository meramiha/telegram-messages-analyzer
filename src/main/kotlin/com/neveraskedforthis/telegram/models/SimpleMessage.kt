package com.neveraskedforthis.telegram.models

import kotlinx.serialization.Serializable

@Serializable
data class SimpleMessage(
    val fromId: String,
    var text: String,
)