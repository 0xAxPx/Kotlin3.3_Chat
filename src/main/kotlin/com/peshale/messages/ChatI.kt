package com.peshale.messages

import com.peshale.domain.Chat
import java.util.*

interface ChatI {

    /*
    create new chat
    upon creation return UUID of chat
     */
    fun create(initiatorId: Int, recipientId: Int, message: Message): UUID

    /*
     Upon delete return true otherwise false
     */
    fun  delete(chatId: UUID): Boolean

    /*
    Get chats of user
     */
    fun getChats(ownerId: Int): List<Chat>

    /*
    Create message
     */
    fun addMessage(chatId: UUID, message: Message)

    /*
    Delete message by its id
     */
    fun deleteMessage(chatId: UUID, messageId: Int):Boolean

    /*
    Get chat messages
     */
    fun  getChatMessages(ownerId: Int, chatId: UUID, lastMessageId: Int, count: Int): List<Message>

}