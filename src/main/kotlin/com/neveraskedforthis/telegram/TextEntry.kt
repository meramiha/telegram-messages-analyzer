package com.neveraskedforthis.telegram

import kotlinx.serialization.Serializable

@Serializable
data class TextEntry(
    val type: String,
    val text: String,
)