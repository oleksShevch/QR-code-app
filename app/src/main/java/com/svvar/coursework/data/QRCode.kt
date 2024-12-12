package com.svvar.coursework.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QRCode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,          // e.g., "URL", "WiFi", etc.
    val content: String,       // The actual data, e.g., URL, WiFi credentials
    val imagePath: String,     // Path to the stored QR code image
    val actionType: String,   // e.g., "generate", "scan"
    val timestamp: Long = System.currentTimeMillis() // Creation time
)