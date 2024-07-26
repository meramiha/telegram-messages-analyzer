package com.neveraskedforthis.telegram

import com.neveraskedforthis.telegram.models.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.io.File


fun main(args: Array<String>) {
    val json = Json {
        ignoreUnknownKeys = true

        serializersModule = SerializersModule {
            polymorphic(BaseEntity::class) {
                subclass(Message::class, Message.serializer())
                subclass(Service::class, Service.serializer())
            }
        }
    }
    val filesWithChat = args.map(::File)

    val chats = filesWithChat.map { json.decodeFromString<Chat>(it.readText()) }

    val allMessages = chats.asSequence().map { it.messages }.flatten().filterIsInstance<Message>()


    val allTexts = allMessages.map { message ->
        SimpleMessage(
            message.from_id, message.text_entities.filterNot { it.text.isBlank() }.joinToString(" ") { textEntity ->
                textEntity.text.replace("\n", "\\n")
            }
        )
    }

    val polinasIds = listOf(allTexts.first().fromId) // TODO
    val allTextsReplaceIds = allTexts.asIterable().joinIds(polinasIds)

    val allTextsMerged = mergeConsecutiveMessages(allTextsReplaceIds.asIterable())

    @Serializable
    data class GPTMessage(val role: String, val content: String)

    val gptMessages =
        allTextsMerged.zipWithNext().mapNotNull { (p, d) ->
            if (!polinasIds.contains(p.fromId)) null
            else
                mapOf(
                    "messages" to
                            listOf(
                                GPTMessage(
                                    "system",
                                    "You are Полина's boyfriend"
                                ),

                                GPTMessage("user", p.text),
                                GPTMessage("assistant", d.text)
                            )
                )
        }
    File("result.jsonl").writeText(gptMessages.joinToString("\n") { json.encodeToString(it) })
}
