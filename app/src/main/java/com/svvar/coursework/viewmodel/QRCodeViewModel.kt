package com.svvar.coursework.viewmodel

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

    fun saveQRCode(context: Context, type: String, content: String, bitmap: Bitmap, actionType: String) {
        viewModelScope.launch {
            val filename = "qr_${System.currentTimeMillis()}"
            val imagePath = ImageUtils.saveBitmapToInternalStorage(context, bitmap, filename)
            val qrCode = QRCode(
                type = type,
                content = content,
                imagePath = imagePath,
                actionType = actionType
            )

            repository.insert(qrCode)
        }
    }
}