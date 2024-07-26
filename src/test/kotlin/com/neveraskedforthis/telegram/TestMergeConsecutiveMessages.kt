package com.neveraskedforthis.telegram

import com.neveraskedforthis.telegram.models.SimpleMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestMergeConsecutiveMessages {
    @Test
    fun `test merge consecutive messages`() {
        val messages = listOf(
            SimpleMessage(
                "danya",
                "a"
            ),
            SimpleMessage(
                "danya",
                "b"
            ),
            SimpleMessage(
                "danya",
                "c"
            ),
            SimpleMessage(
                "polina",
                "1"
            ),
            SimpleMessage(
                "polina",
                "2"
            ),
            SimpleMessage(
                "polina",
                "3"
            ),
            SimpleMessage(
                "danya",
                "d"
            ),
            SimpleMessage(
                "danya",
                "e"
            ),
        )


        assertEquals(
            listOf(
                SimpleMessage("danya", "a\nb\nc"),
                SimpleMessage("polina", "1\n2\n3"),
                SimpleMessage("danya", "d\ne"),
            ), mergeConsecutiveMessages(messages)
        )
    }

}