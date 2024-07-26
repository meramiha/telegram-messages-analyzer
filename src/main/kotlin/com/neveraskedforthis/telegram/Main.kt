package com.neveraskedforthis.telegram

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

    val allLengthsInWords = allTexts.map { it.split(" ", "\n", "\t") }.map { it.size }


    println("with average length ${allLengthsInCharactersExcludingWhitespace.average()} characters (excluding whitespace)")
    println("with average length ${allLengthsInCharactersIncludingWhitespace.average()} characters (including whitespace)")
    println("with average length ${allLengthsInWords.average()} words ")
}