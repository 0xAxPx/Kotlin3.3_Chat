package com.peshale.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Util {

    companion object {
        fun localDateTime2String(localDateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            return localDateTime.format(formatter)
        }
    }
}