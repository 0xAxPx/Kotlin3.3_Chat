package com.peshale.users

import com.peshale.domain.Chat
import java.util.*

data class User(val id: Int) {
    private val myChats = LinkedList<Chat>()
    private val chattingWith = LinkedList<User>()

    fun addChat(chat: Chat) {
        this.myChats.add(chat)
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
}
