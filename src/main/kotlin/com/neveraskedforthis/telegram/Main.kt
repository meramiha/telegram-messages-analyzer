package com.neveraskedforthis.telegram

import com.neveraskedforthis.telegram.models.*
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
                "$START_TOKEN${textEntity.text.replace("\n", "\\n")}$END_TOKEN"
            }
        )
    }

    val polinasIds = listOf<String>(allTexts.first().fromId) // TODO
    val allTextsReplaceIds = allTexts.asIterable().joinIds(polinasIds)

    val allTextsMerged = mergeConsecutiveMessages(allTextsReplaceIds.asIterable())

    val dataset =
        prepareData(allTextsMerged, allTextsMerged.first().fromId, allTextsMerged.drop(1).first().fromId)



    File("result.csv").writeText(dataset.joinToString("\n") { "${it.first}|${it.second}" })
}


fun prepareData(messages: List<SimpleMessage>, requestId: String, responseId: String): List<Pair<String, String>> {
    val dataPairs = mutableListOf<Pair<String, String>>()

    var currentRequest = ""
    var currentResponseTokens = mutableListOf<String>()

    for (message in messages) {
        val tokens = message.text.split(" ")

        if (message.fromId == requestId) {
            if (currentResponseTokens.isNotEmpty()) {
                for (token in currentResponseTokens) {

                    dataPairs.add(currentRequest to token)
                    currentRequest += " $token"
                }
            }

            currentRequest = message.text
            currentResponseTokens.clear()
        } else if (message.fromId == responseId) {
            currentResponseTokens = tokens.toMutableList()
        }
    }

    return dataPairs
}