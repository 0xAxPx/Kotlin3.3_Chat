package com.peshale.domain

import com.peshale.messages.Message
import java.util.*

class ChatRepository {

    private val userChats = LinkedList<Chat>()

    fun cleanUserChat() {
        userChats.clear()
    }

    fun getUserChats(): LinkedList<Chat> {
        return userChats
    }

    fun ifChatExist(initiatorId: Int, recipientId: Int): Chat? {
        return userChats.find { it.ownerId == initiatorId && it.recipientId == recipientId}
    }

    fun deleteChat(uuid: UUID): Boolean {
        return userChats.removeIf {it.uuid == uuid}
    }

    fun getChat(uuid: UUID): Chat? {
        return userChats.find { it.uuid == uuid }
    }

    fun addMessageToChat(chat: Chat, isOwner: Boolean) {
        if (isOwner) {
            val message = chat.message
            val newMessage = message.copy(ownerId = message.from, isIncoming = Message.Incoming.N)
            chat.messages.add(newMessage)
            //userChats.add(chat)
        } else {
            val message = chat.message
            val newMessage = message.copy(ownerId = message.to, isIncoming = Message.Incoming.Y)
            chat.messages.add(newMessage)
            //userChats.add(chat)
        }
        if (!userChats.contains(chat)) {
            userChats.add(chat)
        }
        println("Chat ${chat.uuid}: number of messages: ${chat.messages.size}")
    }

    fun getChatByUUID(uuid: UUID): Chat {
        return userChats.find { it.uuid == uuid }!!
    }
}