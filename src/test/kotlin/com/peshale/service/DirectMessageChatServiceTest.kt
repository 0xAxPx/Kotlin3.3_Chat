package com.peshale.service

import com.peshale.users.User
import com.peshale.users.UsersRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

internal class DirectMessageChatServiceTest {

    @Test
    fun `test create chat and each user should have chat created`() {
        //assuming that user exists i.e. authorized - so all users should be in userRepo
        val usersRepository = UsersRepository()
        val user1 = User(1)
        val user2 = User(2)
        val id1 = usersRepository.add(user1)
        val id2 = usersRepository.add(user2)

        //create chat
        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        assertTrue(2 == usersRepository.repo.size)
        assertTrue(0 == user1.getChats().size)
        assertTrue(0 == user2.getChats().size)

        //create chat
        val uuidChat = directChat.create(id1, id2, "Hello, Johny!")
        Assertions.assertNotNull(uuidChat)
        assertTrue(1 == user1.getChats().size)
        assertTrue(1 == user2.getChats().size)
        assertTrue(1 == chatRepository.chatRepository.size)
        val user1Message = user1.getChatByUUID(uuidChat).getMessages(false)!!.first()
        val user2Message = user2.getChatByUUID(uuidChat).getMessages(false)!!.first()
        assertTrue("Hello, Johny!" == user1Message.text)
        assertTrue("Hello, Johny!" == user2Message.text)
    }

    @Test
    fun `test try to create chat for yourself`() {
        val usersRepository = UsersRepository()
        val defaultUser = User(1)
        val id1 = usersRepository.add(defaultUser)

        val chatRepository = ChatRepository()
        val directChat = DirectMessageChatService(usersRepository, chatRepository)
        assertTrue(1 == usersRepository.repo.size)
        assertTrue(0 == defaultUser.getChats().size)

        //create chat
        val exception = Assertions.assertThrows(RuntimeException::class.java) {
            directChat.create(id1, id1, "Hi, Donald!")
        }
        assertTrue("You can't create direct chat with yourself" == exception.message)
    }
}

@Test
fun add() {
}

@Test
fun delete() {
}

@Test
fun getChats() {
}

@Test
fun createMessage() {
}

@Test
fun deleteMessage() {
}

@Test
fun editMessage() {
}
