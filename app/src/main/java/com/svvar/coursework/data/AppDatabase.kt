package com.svvar.coursework.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QRCode::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qrCodeDao(): QRCodeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qr_code_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}