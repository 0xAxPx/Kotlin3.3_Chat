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

    override fun create(initiatorId: Int, recipientId: Int): UUID {
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
                    messageTo = recipientId,
                    dateCreated = dateCreated,
                    isDeleted = false
            )
            chatRepository.addChat(uuid, chat)
            initiator?.addChat(chat)
            //initiator.addUser(chatWith)
            chatWith?.addChat(chat)
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
        return chatRepository.deleteChat(chatId)
    }

    override fun getChats(ownerId: Int): List<Chat> {
        val chatList = chatRepository.getChats(ownerId)
        chatList.forEach { c ->
            run {
                println("Number of incoming messages: ${c.incomingMessages.size}")
                println("Number outgoing messages: ${c.outgoingMessages.size}")
            }
        }
        return chatList
    }

    override fun createMessage(chatId: UUID, from: Int, to: Int, message: Message): Boolean {
//        val initiator = this.usersRepository.getUser(from)
//        val chatWith = this.usersRepository.getUser(to)
//        initiator.getChatByUUID(chatId).addMessage(message, isIncoming = false)
//        chatWith.getChatByUUID(chatId).addMessage(message, isIncoming = true)
//        val chat = chatRepository.chatRepository.get(chatId)
//        return chat!!.addMessage(message)
        return false
    }

    override fun deleteMessage(chatId: UUID, messageId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun editMessage(chatId: UUID, messageId: Long, text: String): Message {
        TODO("Not yet implemented")
    }
}