package com.example.teamproject.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class WaterRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val amount: Int,  // ml
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val note: String = ""
) {
    fun getFormattedTime(): String {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun getFormattedDate(): String {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}
