package com.peshale.service

import com.peshale.domain.Chat
import com.peshale.domain.ChatRepository
import com.peshale.messages.ChatI
import com.peshale.messages.Message
import com.peshale.users.UsersRepository
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*

class DirectMessageChatService(val usersRepository: UsersRepository, val chatRepository: ChatRepository) : ChatI {

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
        val dateCreated = LocalDateTime.now()
        val chat: Chat
        if (initiatorId != recipientId) {
            chat = Chat(
                    id = uuid,
                    ownerId = initiatorId,
                    recipientId = recipientId,
                    dateCreated = dateCreated,
                    message = message,
                    isDeleted = false
            )

            chatRepository.addMessageToChat(chat, true)
            chatRepository.addMessageToChat(chat, false)
        } else {
            throw RuntimeException("You can't create direct chat with yourself")
        }
        return uuid;
    }

    override fun delete(chatId: UUID): Boolean {
        val chat = chatRepository.getChat(chatId)
        chatRepository.deleteChat(chatId)
        if (chat != null) {
            usersRepository.getUser(chat.ownerId)?.deleteChat(chat)
            usersRepository.getUser(chat.recipientId)?.deleteChat(chat)
        }
        return chatRepository.deleteChat(chatId)
    }

    //get chats for specific user and there are messages with Unread = True
    override fun getChats(ownerId: Int): List<Chat> {
        val userChats = chatRepository.getUserChats()
        return userChats.filter { it.ownerId == ownerId && !it.messages.isEmpty() }
    }

    override fun addMessage(chatId: UUID, message: Message) {
        val chat = chatRepository.getChatByUUID(chatId)
        chat.message = message
        chatRepository.addMessageToChat(chat, true)
        chatRepository.addMessageToChat(chat, false)
    }

    override fun deleteMessage(chatId: UUID, messageId: Int): Boolean {
        val chat = chatRepository.getChatByUUID(chatId)
        return chat.messages.removeIf { it.id == messageId }
    }

    //getting all messages where ownerId == userId i.e. we return only user's message not all messages from the chat
    override fun getChatMessages(ownerId: Int, chatId: UUID, lastMessageId: Int, count: Int): List<Message> {
        val chat = chatRepository.getChatByUUID(chatId)
        //sorted by id in descending order
        chat.messages.sortWith(compareByDescending { it.id })
        val filteredMessages = chat.messages.filter { it.ownerId == ownerId && it.id > lastMessageId }.take(count)
        //make all messages are Unread = False i.e user read them
        filteredMessages.forEach { m ->
            m.isUnread = false
        }
        return filteredMessages
    }
}