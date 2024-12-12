package com.svvar.coursework.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QRCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qrCode: QRCode)

    @Delete
    suspend fun delete(qrCode: QRCode)

    @Query("SELECT * FROM qr_codes ORDER BY timestamp DESC")
    fun getAllQRCodes(): Flow<List<QRCode>>
}