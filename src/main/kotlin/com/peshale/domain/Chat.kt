package com.peshale.domain

import com.peshale.messages.Message
import java.time.LocalDateTime
import java.util.*

class Chat(id: UUID, ownerId: Int, recipientId: Int, dateCreated: LocalDateTime, message: Message, isDeleted: Boolean) {
    val uuid: UUID
    val ownerId: Int
    val recipientId: Int
    val localDateTime: LocalDateTime
    var message: Message
    val isDeleted: Boolean
    val messages: LinkedList<Message>

    init {
        this.uuid = id
        this.ownerId = ownerId
        this.recipientId = recipientId
        this.localDateTime = dateCreated
        this.message = message
        this.isDeleted = isDeleted
        this.messages = LinkedList<Message>()
    }
}