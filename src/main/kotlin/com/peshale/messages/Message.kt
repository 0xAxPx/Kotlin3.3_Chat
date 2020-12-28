package com.peshale.messages

import java.time.LocalDateTime

data class Message (
    val ownerId: Int,
    val text: String,
    val dateCreated: LocalDateTime,
    val isDeleted: Boolean = false)
{

}
