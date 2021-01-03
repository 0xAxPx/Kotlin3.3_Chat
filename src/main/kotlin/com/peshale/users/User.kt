package com.peshale.users

import com.peshale.domain.Chat
import java.util.*

data class User(val id: Int) {

    private val myChats = LinkedList<Chat>()

    fun getChats(): LinkedList<Chat> {
        return this.myChats
    }

    fun deleteChat(chat: Chat): Boolean {
        return myChats.remove(chat)
    }
}
