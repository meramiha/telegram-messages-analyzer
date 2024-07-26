package com.neveraskedforthis.telegram.models

import kotlinx.serialization.Serializable

@Serializable
data class TextEntry(
    val type: String,
    val text: String,
)