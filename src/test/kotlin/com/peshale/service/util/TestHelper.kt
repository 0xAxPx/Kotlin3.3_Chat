package com.peshale.service.util

import com.peshale.messages.Message
import java.time.LocalDateTime
class TestHelper {

    companion object {
        fun testMessage(ownerId: Int, nextText: String): Message {
            return Message(
                    ownerId = ownerId,
                    text = nextText,
                    dateCreated = LocalDateTime.now(),
                    isMissing = false
            )
        }
    }
}