package com.neveraskedforthis.telegram

import com.neveraskedforthis.telegram.models.SimpleMessage

fun mergeConsecutiveMessages(messages: Iterable<SimpleMessage>): List<SimpleMessage> {
    val result = mutableListOf<SimpleMessage>()
    var prevMessage: SimpleMessage? = null

    for (message in messages) {
        if (prevMessage != null && prevMessage.fromId == message.fromId) {
            prevMessage = prevMessage.apply {
                this.text += message.text
            }
        } else {
            prevMessage = SimpleMessage(message.fromId, message.text)
            result.add(prevMessage)
        }
    }

    return result
}

fun Iterable<SimpleMessage>.joinIds(ids: List<String>): List<SimpleMessage> {
    val id = ids.first()
    return map { SimpleMessage(if (ids.contains(it.fromId)) id else it.fromId, it.text) }
}

const val START_TOKEN = "<START>"
const val END_TOKEN = "<END>"