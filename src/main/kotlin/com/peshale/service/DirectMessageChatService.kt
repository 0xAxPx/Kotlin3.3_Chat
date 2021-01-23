package com.peshale.service

import com.peshale.domain.Chat
import com.peshale.domain.ChatRepository
import com.peshale.messages.ChatI
import com.peshale.messages.Message
import com.peshale.users.UsersRepository
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*

class DirectMessageChatService(val usersRepo: UsersRepository, val chatRepo: ChatRepository) : ChatI {

    override fun create(initiatorId: Int, recipientId: Int, message: Message): UUID {
        //check if chat participants exists
        if (usersRepo.getUser(initiatorId) == null && usersRepo.getUser(recipientId) == null) {
            throw RuntimeException("Check if both users exist with id: $initiatorId, $recipientId")
        }

        //check if chat already exist
        if (chatRepo.ifChatExist(initiatorId, recipientId) != null) {
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

            chatRepo.addMessageToChat(chat, true)
            chatRepo.addMessageToChat(chat, false)
        } else {
            throw RuntimeException("You can't create direct chat with yourself")
        }
        return uuid;
    }

    override fun delete(chatId: UUID): Boolean {
        val chat = chatRepo.getChat(chatId)
        chatRepo.deleteChat(chatId)
        if (chat != null) {
            usersRepo.getUser(chat.ownerId)?.deleteChat(chat)
            usersRepo.getUser(chat.recipientId)?.deleteChat(chat)
        }
        return chatRepo.deleteChat(chatId)
    }

    //get chats for specific user and there are messages with Unread = True
    override fun getChats(ownerId: Int): List<Chat> {
        return chatRepo.getUserChats()
                .filter { it.ownerId == ownerId && !it.messages.isEmpty() }
    }

    override fun addMessage(chatId: UUID, message: Message) {
        val chat = chatRepo.getChatByUUID(chatId)
        chat.message = message
        chatRepo.addMessageToChat(chat, true)
        chatRepo.addMessageToChat(chat, false)
    }

    override fun deleteMessage(chatId: UUID, messageId: Int): Boolean {
        val chat = chatRepo.getChatByUUID(chatId)
        return chat.messages.removeIf { it.id == messageId }
    }

    //getting all messages where ownerId == userId i.e. we return only user's message not all messages from the chat
    override fun getChatMessages(ownerId: Int, chatId: UUID, lastMessageId: Int, count: Int): List<Message> {
        val chat = chatRepo.getChatByUUID(chatId)
        //sorted by id in descending order
        chat.messages.sortWith(compareByDescending { it.id })
        return chat.messages.asSequence()
                        .filter { it.ownerId == ownerId && it.id > lastMessageId }
                        .take(count)
                        .forEach {m -> m.isUnread = false}
                        .let { listOf() }
    }
}