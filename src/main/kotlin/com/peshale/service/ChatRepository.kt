package com.peshale.service

import com.peshale.domain.Chat
import java.util.*

class ChatRepository {

    val chatRepository = LinkedList<Chat>()

    fun addChat(chat: Chat) {
        this.chatRepository.add(chat)
    }


}