package com.svvar.coursework.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QRCode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val content: String,
    val imagePath: String,
    val actionType: String,
    val timestamp: Long = System.currentTimeMillis()
)