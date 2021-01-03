package com.peshale.users

import com.peshale.domain.Chat
import com.peshale.messages.Message
import java.util.*

data class User(val id: Int) {
    val incoming: LinkedList<Message> = LinkedList()
    val outgoing: LinkedList<Message> = LinkedList()

    private val myChats = LinkedList<Chat>()
    private val chattingWith = LinkedList<User>()

    fun addChat(chat: Chat, isOwner: Boolean) {
        if (isOwner) {
            this.outgoing.add(chat.message)
            myChats.add(chat)
        } else {
            this.incoming.add(chat.message)
            myChats.add(chat)
        }
    }

    fun addChat(chat: Chat): Boolean {
        return myChats.add(chat)
    }

    fun getChats(): LinkedList<Chat> {
        return this.myChats
    }

    fun getChatByUUID(uuid: UUID): Chat {
        return myChats.first { it.uuid == uuid }
    }

    fun addUser(user: User) {
        this.chattingWith.add(user)
    }

    fun getCollaboration(): LinkedList<User> {
        return this.chattingWith
    }

    fun deleteChat(chat: Chat): Boolean {
        return myChats.remove(chat)
    }
}
