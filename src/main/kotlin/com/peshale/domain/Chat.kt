package com.peshale.domain

import com.peshale.messages.Message
import java.time.LocalDateTime
import java.util.*

class Chat(id: UUID, ownerId: Int, messageTo: Int, dateCreated: LocalDateTime, isDeleted: Boolean) {
    val uuid: UUID
    val ownerId: Int
    val messageTo: Int
    val localDateTime: LocalDateTime
    val incomingMessages: LinkedList<Message>
    val outgoingMessages: LinkedList<Message>
    val isDeleted: Boolean

    init {
        this.uuid = id
        this.ownerId = ownerId
        this.messageTo = messageTo
        this.localDateTime = dateCreated
        this.incomingMessages = LinkedList<Message>()
        this.outgoingMessages = LinkedList<Message>()
        this.isDeleted = isDeleted
    }

    fun addMessage(message: Message, isIncoming: Boolean): Boolean {
        if (isIncoming) {
            message.incoming = Message.Incoming.Y
            return this.incomingMessages.add(message)
        } else {
            message.incoming = Message.Incoming.N
            return this.outgoingMessages.add(message)
        }
    }

//    //generic
//    fun addMessage(message: Message): Boolean {
//        return messages.add(message)
//    }
}