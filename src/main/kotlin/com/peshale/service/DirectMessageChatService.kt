package com.peshale.service

import com.peshale.domain.Chat
import com.peshale.messages.ChatI
import com.peshale.messages.Message
import com.peshale.users.User
import com.peshale.users.UsersRepository
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class DirectMessageChatService(val usersRepository: UsersRepository) : ChatI {

    fun addUserToRepo(user: User) {
        usersRepository.add(user)
    }

    override fun create(ownerId: Int, messageTo: Int): UUID {
        val uuid = UUID.randomUUID()
        val owner = this.usersRepository.getUser(ownerId)
        val chatWith = this.usersRepository.getUser(messageTo)

        if (ownerId != messageTo) {
            val chat = Chat(
                id = uuid,
                owner = owner,
                dateCreated = LocalDateTime.now(),
            )
            owner.addChat(chat)
            owner.addUser(chatWith)
            chatWith.addChat(chat)
            chatWith.addUser(owner)
        } else {
            throw RuntimeException("You can't create direct chat with yourself")
        }
        return uuid;
    }

    override fun add(chatId: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(chatId: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun getChats(ownerId: Int): ArrayList<Chat> {
        TODO("Not yet implemented")
    }

    override fun createMessage(chatId: UUID): Long {
        TODO("Not yet implemented")
    }

    override fun deleteMessage(chatId: UUID, messageId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun editMessage(chatId: UUID, messageId: Long, text: String): Message {
        TODO("Not yet implemented")
    }
}