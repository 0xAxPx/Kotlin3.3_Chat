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


class DirectMessageChatService(val usersRepository: UsersRepository, val chatRepository: ChatRepository) : ChatI {

    fun addUserToRepo(user: User) {
        usersRepository.add(user)
    }

    override fun create(initiatorId: Int, messageTo: Int, text: String): UUID {
        val uuid = UUID.randomUUID()
        val initiator = this.usersRepository.getUser(initiatorId)
        val chatWith = this.usersRepository.getUser(messageTo)
        val dateCreated = LocalDateTime.now()
        if (initiatorId != messageTo) {
            val chat = Chat(
                    id = uuid,
                    ownerId = initiatorId,
                    messageTo = messageTo,
                    message = Message(ownerId = initiatorId, text = text, dateCreated = dateCreated, isMissing = false, isDeleted = false),
                    dateCreated = dateCreated,
                    isDeleted = false
            )
            chatRepository.addChat(chat)
            initiator.addChat(chat)
            //initiator.addUser(chatWith)
            chatWith.addChat(chat)
            //chatWith.addUser(initiator)
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

    override fun getChats(ownerId: Int, unread: Boolean): ArrayList<Chat> {
        TODO("Not yet implemented")

    }

    override fun createMessage(chatId: UUID, from: Int): Long {
        TODO("Not yet implemented")
    }

    override fun deleteMessage(chatId: UUID, messageId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun editMessage(chatId: UUID, messageId: Long, text: String): Message {
        TODO("Not yet implemented")
    }
}