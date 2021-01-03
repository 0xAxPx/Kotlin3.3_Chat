package com.peshale.messages

import com.peshale.util.Util.Companion.localDateTime2String
import java.time.LocalDateTime

data class Message(
        val id: Int,
        val from: Int,
        val to: Int,
        val ownerId: Int = 0,
        val text: String,
        var isIncoming: Incoming = Incoming.N,
        val dateCreated: String,
        var isUnread: Boolean,
        val isDeleted: Boolean = false) {

    companion object {
        fun createMessage(id: Int, from: Int, to: Int, text: String, dateCreated: LocalDateTime): Message {
            return Message(id = id, from = from, to = to, text = text, dateCreated = localDateTime2String(dateCreated), isUnread = true, isDeleted = false)
        }
    }

    enum class Incoming {
        Y, N
    }
}

