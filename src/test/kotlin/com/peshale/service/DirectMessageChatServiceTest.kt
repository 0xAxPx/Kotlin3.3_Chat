package com.peshale.service

import com.peshale.users.User
import com.peshale.users.UsersRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import java.util.*

internal class DirectMessageChatServiceTest {

    @Test
    fun `test create chat and each user should have chat created`() {
        val usersRepository = UsersRepository()
        val defaultUser = User(1)
        val anotherUser = User(2)
        val id1 = usersRepository.add(defaultUser)
        val id2 = usersRepository.add(anotherUser)
        val directChat = DirectMessageChatService(usersRepository)
        assertTrue(2 == usersRepository.repo.size)
        assertTrue(0 == defaultUser.getChats().size)
        assertTrue(0 == anotherUser.getChats().size)

        //create chat
        val uuidChat = directChat.create(id1, id2)
        assertTrue(1 == defaultUser.getChats().size)
        assertTrue(1 == anotherUser.getChats().size)
        assertTrue(anotherUser == defaultUser.getCollaboration()[0])
        assertTrue(defaultUser == anotherUser.getCollaboration()[0])
    }

    @Test
    fun `test try to create chat for yourself`() {
        val usersRepository = UsersRepository()
        val defaultUser = User(1)
        val id1 = usersRepository.add(defaultUser)
        val directChat = DirectMessageChatService(usersRepository)
        assertTrue(1 == usersRepository.repo.size)
        assertTrue(0 == defaultUser.getChats().size)

        //create chat
        val exception = Assertions.assertThrows(RuntimeException::class.java) {
            directChat.create(id1, id1)
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
