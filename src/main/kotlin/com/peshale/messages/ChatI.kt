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
    add new message to existing chat
    upon adding returns true otherwise false
     */
    fun add(chatId: UUID): Boolean

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
    fun addMessage(chatId: UUID, from: Int, to: Int, message: Message)

    /*
    Delete message by its id
     */
    fun deleteMessage(chatId: UUID, messageId: Long):Boolean

    /*
    Edit message, returns new message
     */
    fun  editMessage(chatId: UUID, messageId: Long, text: String): Message

}