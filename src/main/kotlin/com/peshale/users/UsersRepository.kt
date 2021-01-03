package com.peshale.users

import java.util.*

class UsersRepository {

    val repo = LinkedList<User>()

    fun add(user: User): Boolean {
        return repo.add(user)
    }

    fun getUser(id: Int): User? {
        return repo.find { it.id == id }
    }
}
