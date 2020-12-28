package com.peshale.domain

import com.peshale.messages.Message
import com.peshale.users.User
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

data class Chat(
    val id: UUID,
    val owner: User,
    val dateCreated: LocalDateTime,
    val isDeleted: Boolean = false) {

    companion object {
        private val messagesMap = HashMap<Long, Message>()

        fun getChatMessages(): HashMap<Long, Message> {
            return messagesMap
        }
    }
}
