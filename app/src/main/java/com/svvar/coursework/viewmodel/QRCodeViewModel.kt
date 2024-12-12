package com.svvar.coursework.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.svvar.coursework.data.AppDatabase
import com.svvar.coursework.data.QRCode
import com.svvar.coursework.data.QRCodeRepository
import com.svvar.coursework.utils.ImageUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QRCodeViewModel(application: android.app.Application) : AndroidViewModel(application) {

    private val repository: QRCodeRepository

    val allQRCodes: StateFlow<List<QRCode>>
    init {
        val qrCodeDao = AppDatabase.getDatabase(application).qrCodeDao()
        repository = QRCodeRepository(qrCodeDao)
        allQRCodes = repository.allQRCodes
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    /**
     * Saves the generated QR code bitmap and its associated data.
     *
     * @param context The application context.
     * @param type The type of QR code (e.g., "URL", "WiFi").
     * @param content The text content converted into the QR code.
     * @param bitmap The generated QR code bitmap.
     */
    fun saveQRCode(context: Context, type: String, content: String, bitmap: Bitmap, actionType: String) {
        viewModelScope.launch {
            // Generate a unique filename using the current timestamp
            val filename = "qr_${System.currentTimeMillis()}"

            // Save the bitmap to internal storage and get the file path
            val imagePath = ImageUtils.saveBitmapToInternalStorage(context, bitmap, filename)

            // Create a QRCode object with the provided data
            val qrCode = QRCode(
                type = type,
                content = content,
                imagePath = imagePath,
                actionType = actionType
            )

            // Insert the QRCode object into the database
            repository.insert(qrCode)
        }
    }
}