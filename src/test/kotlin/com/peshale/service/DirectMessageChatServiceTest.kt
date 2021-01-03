package com.peshale.service

import com.peshale.domain.ChatRepository
import com.peshale.messages.Message
import com.peshale.messages.Message.Companion.createMessage
import com.peshale.users.User
import com.peshale.users.UsersRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class DirectMessageChatServiceTest {

    @Test
    fun cleanUp() {
        ChatRepository().cleanUserChat()
    }

    @Test
    fun `test create chat and each user should have chat created and incoming and outgoing messages respectevely`() {
        //assuming that user exists i.e. authorized - so all users should be in userRepo
        val usersRepository = UsersRepository()
        val user1 = User(1)
        val user2 = User(2)
        usersRepository.add(user1)
        usersRepository.add(user2)

        //create chat
        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        assertTrue(2 == usersRepository.repo.size)

        //create chat
        val textMessage = "Hello, buddy!"
        val uuidChat = directChat.create(user1.id, user2.id, createMessage(1, from = user1.id, to = user2.id, text = textMessage, dateCreated = LocalDateTime.now()))
        Assertions.assertNotNull(uuidChat)
        val chat = chatRepository.getChatByUUID(uuidChat)

        //check all incoming messages for user1
        val userOutgoingMessage = chat.messages.find { it.ownerId == user1.id }
        assertTrue(Message.Incoming.N == userOutgoingMessage!!.isIncoming)
        //check all incoming messages for user2
        val userIncomingMessage = chat.messages.find { it.ownerId == user2.id }
        assertTrue(Message.Incoming.Y == userIncomingMessage!!.isIncoming)
        //check both incoming and outgoing messages have the same text
        assertTrue(userIncomingMessage.text == userOutgoingMessage.text)

        //add one more message
        val newText = "Message from 1 to 2 userId"
        directChat.addMessage(chatId = uuidChat, createMessage(2, from = user1.id, to = user2.id, text = newText, dateCreated = LocalDateTime.now()))

        //check that user1 has 2 outgoing messages
        var user1Messages = chat.messages.filter { it.ownerId == user1.id }
        assertTrue(2 == user1Messages.size)
        user1Messages.forEach {
            assertTrue(it.isIncoming == Message.Incoming.N)
        }

        //check that user2 has 2 incoming messages
        var user2Messages = chat.messages.filter { it.ownerId == user2.id }
        assertTrue(2 == user2Messages.size)
        user2Messages.forEach {
            assertTrue(it.isIncoming == Message.Incoming.Y)
        }

        //messages have the same text
        assertTrue(user1Messages[0].text == user2Messages[0].text)
        assertTrue(user1Messages[1].text == user2Messages[1].text)

        //add one more message, change sender and recipients
        directChat.addMessage(chatId = uuidChat, createMessage(3, from = user2.id, to = user1.id, text = "Message from 2 to 1 userId", dateCreated = LocalDateTime.now()))

        //check user1 has 1 incoming message and user2 has 1 outgoing message
        user1Messages = chat.messages.filter { it.ownerId == user1.id && it.isIncoming == Message.Incoming.Y }
        assertTrue(1 == user1Messages.size)
        assertTrue("Message from 2 to 1 userId" == user1Messages.get(0).text)
        user1Messages.forEach {
            assertTrue(it.isUnread)
        }

        user2Messages = chat.messages.filter { it.ownerId == user2.id && it.isIncoming == Message.Incoming.N }
        assertTrue(1 == user2Messages.size)
        assertTrue("Message from 2 to 1 userId" == user2Messages.get(0).text)
        user2Messages.forEach {
            assertTrue(it.isUnread)
        }
    }

    @Test
    fun `test getting messages sorted by id  properly`() {
        val usersRepository = UsersRepository()
        val user1 = User(1)
        val user2 = User(2)
        usersRepository.add(user1)
        usersRepository.add(user2)

        //create chat
        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        val uuidChat = directChat.create(user1.id, user2.id, createMessage(1, from = user1.id, to = user2.id, text = "Message 1", dateCreated = LocalDateTime.now()))

        //filtering id in (1,2,3,4,5,6)
        directChat.addMessage(chatId = uuidChat, createMessage(6, from = user2.id, to = user1.id, text = "Message 6", dateCreated = LocalDateTime.now()))
        directChat.addMessage(chatId = uuidChat, createMessage(5, from = user2.id, to = user1.id, text = "Message 5", dateCreated = LocalDateTime.now()))
        directChat.addMessage(chatId = uuidChat, createMessage(4, from = user2.id, to = user1.id, text = "Message 4", dateCreated = LocalDateTime.now()))
        directChat.addMessage(chatId = uuidChat, createMessage(3, from = user2.id, to = user1.id, text = "Message 3", dateCreated = LocalDateTime.now()))
        directChat.addMessage(chatId = uuidChat, createMessage(2, from = user2.id, to = user1.id, text = "Message 2", dateCreated = LocalDateTime.now()))
        directChat.addMessage(chatId = uuidChat, createMessage(7, from = user2.id, to = user1.id, text = "Message 7", dateCreated = LocalDateTime.now()))

        val result = directChat.getChatMessages(user1.id, uuidChat, 2, 4)
        val expectedList = listOf<Int>(4,5,6,7)
        result.forEach {
            assertFalse(it.isUnread)
        }
        result.forEach {
            assertTrue(expectedList.contains(it.id))
        }
    }


    @Test
    fun `test if user does not exist throw exception while creating a chat`() {
        //assuming that user exists i.e. authorized - so all users should be in userRepo
        val usersRepository = UsersRepository()
        val user1 = User(1)
        val user2 = User(2)

        //create chat
        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        assertTrue(0 == usersRepository.repo.size)
        assertTrue(0 == user1.getChats().size)
        assertTrue(0 == user2.getChats().size)

        //create chat
        val exception = Assertions.assertThrows(RuntimeException::class.java) {
            directChat.create(user1.id, user2.id, createMessage(6, from = user2.id, to = user1.id, text = "Message 6", dateCreated = LocalDateTime.now()))
        }
        assertTrue("Check if both users exist with id: 1, 2" == exception.message)
    }

    @Test
    fun `test try to create chat for yourself`() {
        val usersRepository = UsersRepository()
        val user1 = User(1)
        usersRepository.add(user1)

        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        assertTrue(1 == usersRepository.repo.size)
        assertTrue(0 == user1.getChats().size)

        //create chat
        val exception = Assertions.assertThrows(RuntimeException::class.java) {
            directChat.create(user1.id, user1.id, createMessage(6, from = user1.id, to = user1.id, text = "Message 6", dateCreated = LocalDateTime.now()))
        }
        assertTrue("You can't create direct chat with yourself" == exception.message)
    }

    @Test
    fun `test that chat exist should be deleted from chat repo`() {
        val usersRepository = UsersRepository()
        val user1 = User(1)
        val user2 = User(2)
        usersRepository.add(user1)
        usersRepository.add(user2)

        //create chat
        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)

        //create chat
        val uuidChat = directChat.create(user1.id, user2.id, createMessage(6, from = user1.id, to = user1.id, text = "Message 6", dateCreated = LocalDateTime.now()))
        assertTrue(uuidChat == chatRepository.getChat(uuidChat)?.uuid)
        directChat.delete(uuidChat)
        assertNull(chatRepository.getChat(uuidChat)?.uuid)
    }

    @Test
    fun `test getting all user chat where messages exist`() {
        val usersRepository = UsersRepository()
        val user1 = User(1)
        val user2 = User(2)
        val user3 = User(3)
        usersRepository.add(user1)
        usersRepository.add(user2)
        usersRepository.add(user3)

        //create chat
        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        val uuidChat1 = directChat.create(user1.id, user2.id, createMessage(6, from = user1.id, to = user1.id, text = "Message 6", dateCreated = LocalDateTime.now()))
        val uuidChat2 = directChat.create(user3.id, user2.id, createMessage(2, from = user3.id, to = user2.id, text = "Message 2", dateCreated = LocalDateTime.now()))
        assertTrue(2 == chatRepository.getUserChats().size)
        chatRepository.getUserChats().forEach {
            c -> assertTrue(2 == c.messages.size)
        }
        directChat.addMessage(chatId = uuidChat1, createMessage(3, from = user1.id, to = user2.id, text = "Message 3", dateCreated = LocalDateTime.now()))
        directChat.deleteMessage(uuidChat1,6)
        assertTrue(2 == chatRepository.getChat(uuidChat1)!!.messages.size)

        directChat.deleteMessage(uuidChat1,3)
        assertTrue(0 == chatRepository.getChat(uuidChat1)!!.messages.size)
    }

    @Test
    fun `test that an attempt to delete non-exist chat return false`() {
        val usersRepository = UsersRepository()
        //create chat
        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        assertFalse(directChat.delete(UUID.randomUUID()))
    }

    @Test
    fun `test getting unread chat`() {
        val usersRepository = UsersRepository()
        val user1 = User(1)
        val user2 = User(2)
        val user3 = User(3)
        usersRepository.add(user1)
        usersRepository.add(user2)
        usersRepository.add(user3)

        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        val uuidChat1 = directChat.create(user1.id, user2.id, createMessage(6, from = user1.id, to = user1.id, text = "Message 6", dateCreated = LocalDateTime.now()))
        val uuidChat2 = directChat.create(user3.id, user2.id, createMessage(2, from = user3.id, to = user2.id, text = "Message 2", dateCreated = LocalDateTime.now()))
        val uuidChat3 = directChat.create(user1.id, user3.id, createMessage(3, from = user3.id, to = user1.id, text = "Message 3", dateCreated = LocalDateTime.now()))

        //as default all messages are unread until we getAllMessages
        assertTrue(2 == chatRepository.getChat(uuidChat1)!!.messages.filter { it.isUnread }.size)
        assertTrue(2 == chatRepository.getChat(uuidChat2)!!.messages.filter { it.isUnread }.size)
        assertTrue(2 == chatRepository.getChat(uuidChat3)!!.messages.filter { it.isUnread }.size)

        directChat.getChatMessages(user1.id, uuidChat1, 1, 2)
        assertTrue(chatRepository.getChat(uuidChat1)!!.messages.none { it.isUnread })
        assertTrue(2 == chatRepository.getChat(uuidChat2)!!.messages.filter { it.isUnread }.size)
        assertTrue(2 == chatRepository.getChat(uuidChat3)!!.messages.filter { it.isUnread }.size)
    }
}



