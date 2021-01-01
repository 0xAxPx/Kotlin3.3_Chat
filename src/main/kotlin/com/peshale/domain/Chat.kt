package com.peshale.domain

import com.peshale.messages.Message
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Chat(id: UUID, ownerId: Int, messageTo: Int, message: Message, dateCreated: LocalDateTime, isDeleted: Boolean) {
    private val messages: ArrayList<Message>
    val id: UUID
    private val ownerId: Int
    private val messageTo: Int
    private val message: Message
    private val localDateTime: LocalDateTime
    private val isDeleted: Boolean
    private fun addMessage(message: Message) {
            messages.add(message)
    }

    init {
        messages = ArrayList<Message>()
        this.id = id
        this.ownerId = ownerId
        this.messageTo = messageTo
        this.message = message
        this.localDateTime = dateCreated
        this.isDeleted = isDeleted
        this.addMessage(message)
    }

    fun getMessages(isMissing: Boolean): List<Message>? {
        return messages.filter {
            it.isMissing == isMissing
        }
    }
}