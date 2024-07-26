package com.neveraskedforthis.telegram

import com.neveraskedforthis.telegram.models.BaseEntity
import com.neveraskedforthis.telegram.models.Chat
import com.neveraskedforthis.telegram.models.Message
import com.neveraskedforthis.telegram.models.Service
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.io.File
import java.lang.Character.isWhitespace


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

    println("${allMessages.count()} messages")

    val allTexts = allMessages.map {
        it.text_entities.map { textEntity ->
            textEntity.text
        }
    }.flatten()

    val allLengthsInCharactersExcludingWhitespace = allTexts.map { it.filterNot(::isWhitespace).length }
    val allLengthsInCharactersIncludingWhitespace = allTexts.map { it.length }

    val allLengthsInWords = allTexts.map { it.split(Regex("\\s")) }.map { it.size }

    println("with average length ${"%.2f".format(allLengthsInCharactersExcludingWhitespace.average())} characters (excluding whitespace)")
    println("with average length ${"%.2f".format(allLengthsInCharactersIncludingWhitespace.average())} characters (including whitespace)")
    println("with average length ${"%.2f".format(allLengthsInWords.average())} words")
}
