package com.svvar.coursework.data

import kotlinx.coroutines.flow.Flow

class QRCodeRepository(private val qrCodeDao: QRCodeDao) {

    val allQRCodes: Flow<List<QRCode>> = qrCodeDao.getAllQRCodes()

    suspend fun insert(qrCode: QRCode) {
        qrCodeDao.insert(qrCode)
    }

    suspend fun delete(qrCode: QRCode) {
        qrCodeDao.delete(qrCode)
    }
}