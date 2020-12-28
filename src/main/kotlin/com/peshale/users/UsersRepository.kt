package com.peshale.users

import kotlin.collections.HashMap
import kotlin.random.Random

class UsersRepository {

    val repo = HashMap<Int, User>()

    fun add(user: User): Int {
        val id = Random.nextInt(1, Int.MAX_VALUE)
        repo[id] = user
        return id
    }

    fun getUser(id: Int): User {
        return repo.get(id)!!
    }
}
