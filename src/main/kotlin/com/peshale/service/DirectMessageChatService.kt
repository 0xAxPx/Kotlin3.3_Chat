package com.peshale.service

import com.peshale.domain.Chat
import com.peshale.messages.ChatI
import com.peshale.messages.Message
import com.peshale.users.User
import com.peshale.users.UsersRepository
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*

class DirectMessageChatService(val usersRepository: UsersRepository, val chatRepository: ChatRepository) : ChatI {

    fun addUserToRepo(user: User) {
        usersRepository.add(user)
    }

    override fun create(initiatorId: Int, recipientId: Int, message: Message): UUID {
        //check if chat participants exists
        if (usersRepository.getUser(initiatorId) == null && usersRepository.getUser(recipientId) == null) {
            throw RuntimeException("Check if both users exist with id: $initiatorId, $recipientId")
        }

        //check if chat already exist
        if (chatRepository.ifChatExist(initiatorId, recipientId) != null) {
            throw RuntimeException("Chat between $initiatorId and $recipientId exists")
        }

        val uuid = UUID.randomUUID()
        val initiator = this.usersRepository.getUser(initiatorId)
        val chatWith = this.usersRepository.getUser(recipientId)
        val dateCreated = LocalDateTime.now()
        if (initiatorId != recipientId) {
            val chat = Chat(
                    id = uuid,
                    ownerId = initiatorId,
                    recipientId = recipientId,
                    dateCreated = dateCreated,
                    message = message,
                    isDeleted = false
            )

            //new message to recipientId
            //chat.addMessage(message)
            chatRepository.addChat(uuid, chat)
            initiator?.addChat(chat, true)
            chatWith?.addChat(chat, false)
        } else {
            throw RuntimeException("You can't create direct chat with yourself")
        }
        return uuid;
    }

    override fun add(chatId: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(chatId: UUID): Boolean {
        val chat = chatRepository.getChat(chatId)
        if (chat != null) {
            usersRepository.getUser(chat.ownerId)?.deleteChat(chat)
            usersRepository.getUser(chat.recipientId)?.deleteChat(chat)
        }
        return chatRepository.deleteChat(chatId)
    }

    override fun getChats(ownerId: Int): List<Chat> {
        val user = usersRepository.getUser(ownerId)
        return user?.getChats()!!
    }

    override fun addMessage(chatId: UUID, from: Int, to: Int, message: Message) {
        val initiator = this.usersRepository.getUser(from)
        val recipient = this.usersRepository.getUser(to)
        initiator?.outgoing?.add(message)
        recipient?.incoming?.add(message)
    }

    override fun deleteMessage(chatId: UUID, messageId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun editMessage(chatId: UUID, messageId: Long, text: String): Message {
        TODO("Not yet implemented")
    }
}