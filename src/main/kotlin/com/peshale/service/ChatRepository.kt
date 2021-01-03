package com.peshale.service

import com.peshale.domain.Chat
import java.util.*
import kotlin.collections.HashMap

class ChatRepository {

    private val chatRepository = HashMap<UUID, Chat>()

    fun addChat(uuid: UUID, chat: Chat) {
        this.chatRepository.put(uuid, chat)
    }

    fun ifChatExist(initiatorId: Int, messageTo: Int): Chat? {
        return chatRepository.values.find { it.ownerId == initiatorId && it.recipientId == messageTo }
    }

    fun deleteChat(uuid: UUID): Boolean {
        return chatRepository.values.removeIf {
            t -> t.uuid == uuid
        }
    }

    fun getRepoSize(): HashMap<UUID, Chat> {
        return this.chatRepository
    }

    fun getChat(uuid: UUID): Chat? {
        return chatRepository.get(uuid)
    }
}