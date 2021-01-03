package com.peshale.service

import com.peshale.messages.Message
import com.peshale.service.util.TestHelper.Companion.testMessage
import com.peshale.users.User
import com.peshale.users.UsersRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

internal class DirectMessageChatServiceTest {

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
        val message = testMessage(user1.id, textMessage)
        val uuidChat = directChat.create(user1.id, user2.id, message)
        Assertions.assertNotNull(uuidChat)
        assertTrue(1 == user1.getChats().size)
        assertTrue(1 == user2.getChats().size)
        assertTrue(1 == chatRepository.getRepoSize().size)

        //check that user1 has 1 outgoing message
        assertTrue(textMessage == user1.outgoing.get(0).text)
        assertTrue(0 == user1.incoming.size)

        //check that user2 has 1 incoming message
        assertTrue(textMessage == user2.incoming.get(0).text)
        assertTrue(0 == user2.outgoing.size)

        //add one more message
        val newText = "New Message to my friend"
        val newMessage = testMessage(user1.id, newText)
        directChat.addMessage(chatId = uuidChat, from = user1.id, to = user2.id, newMessage)
        //check that user1 has 1 outgoing message
        assertTrue(textMessage == user1.outgoing.get(0).text)
        assertTrue(0 == user1.incoming.size)
        assertTrue(newText == user1.outgoing.get(1).text)
        assertTrue(0 == user1.incoming.size)

        //check that user2 has 1 incoming message
        assertTrue(textMessage == user2.incoming.get(0).text)
        assertTrue(0 == user2.outgoing.size)
        assertTrue(newText == user2.incoming.get(1).text)
        assertTrue(0 == user2.outgoing.size)

    }

    @Test
    fun `test creating chat two times with the same user`() {
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
        assertTrue(0 == user1.getChats().size)
        assertTrue(0 == user2.getChats().size)

        //create chat
        val uuidChat = directChat.create(user1.id, user2.id, testMessage(user1.id, "Hello!"))
        Assertions.assertNotNull(uuidChat)
        assertTrue(1 == user1.getChats().size)
        assertTrue(1 == user2.getChats().size)
        assertTrue(1 == chatRepository.getRepoSize().size)

        val exception = Assertions.assertThrows(RuntimeException::class.java) {
            directChat.create(user1.id, user2.id, testMessage(user1.id, "Hello!"))
        }
        assertTrue("Chat between 1 and 2 exists" == exception.message)
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
            directChat.create(user1.id, user2.id, testMessage(user1.id, "Hello!"))
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
            directChat.create(user1.id, user1.id, testMessage(user1.id, "Hello!"))
        }
        assertTrue("You can't create direct chat with yourself" == exception.message)
    }

    @Test
    fun `test getting chat for user by its id`() {
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
        assertTrue(3 == usersRepository.repo.size)
        assertTrue(0 == user1.getChats().size)
        assertTrue(0 == user2.getChats().size)
        assertTrue(0 == user3.getChats().size)

        //create chat
        val uuidChat1 = directChat.create(user1.id, user2.id, testMessage(user1.id, "Hello!"))
        Assertions.assertNotNull(uuidChat1)
        assertTrue(1 == user1.getChats().size)
        assertTrue(1 == user2.getChats().size)
        assertTrue(0 == user3.getChats().size)
        assertTrue(1 == chatRepository.getRepoSize().size)

        val uuidChat2 = directChat.create(user1.id, user3.id, testMessage(user1.id, "Hello!"))
        Assertions.assertNotNull(uuidChat2)
        assertTrue(2 == user1.getChats().size)
        assertTrue(1 == user2.getChats().size)
        assertTrue(1 == user3.getChats().size)
        assertTrue(2 == chatRepository.getRepoSize().size)

        val uuidChat3 = directChat.create(user3.id, user2.id, testMessage(user1.id, "Hello!"))
        Assertions.assertNotNull(uuidChat3)
        assertTrue(2 == user1.getChats().size)
        assertTrue(2 == user2.getChats().size)
        assertTrue(2 == user3.getChats().size)
        assertTrue(3 == chatRepository.getRepoSize().size)
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
        assertTrue(2 == usersRepository.repo.size)
        assertTrue(0 == user1.getChats().size)
        assertTrue(0 == user2.getChats().size)

        //create chat
        val uuidChat = directChat.create(user1.id, user2.id, testMessage(user1.id, "Hello!"))
        assertTrue(1 == directChat.getChats(user1.id).size)
        directChat.delete(uuidChat)
        assertTrue(directChat.getChats(user2.id).isEmpty())
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
    fun `test each chat user has message with proper incoming property`() {
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
        assertTrue(0 == user1.getChats().size)
        assertTrue(0 == user2.getChats().size)

        //create chat
//        val uuidChat = directChat.create(user1.id, user2.id, testMessage(user1.id, "Hello!"))
//        directChat.createMessage(uuidChat, user1.id, user2.id, Message(ownerId = user1.id,
//                "hello. John!", dateCreated = LocalDateTime.now(), isMissing = false, isDeleted = false))

     //   val messageUser1 = user1.getChatByUUID(uuidChat)

    }
}



