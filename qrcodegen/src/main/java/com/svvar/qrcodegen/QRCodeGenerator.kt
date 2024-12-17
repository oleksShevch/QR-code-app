package com.svvar.qrcodegen

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QRCodeGenerator {

    fun generateQRCode(content: String, width: Int, height: Int): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
            )

            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565) //Bitmap.Config.ARGB_8888) //Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)      // WHITE or TRANSPARENT
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateURLQRCode(url: String, width: Int, height: Int): Bitmap? {
        return generateQRCode(url, width, height)
    }

    fun generatePlainTextQRCode(text: String, width: Int, height: Int): Bitmap? {
        return generateQRCode(text, width, height)
    }

    fun generateEmailQRCode(email: String, subject: String?, body: String?, width: Int, height: Int): Bitmap? {
        val mailto = StringBuilder("mailto:$email")
        if (!subject.isNullOrEmpty()) {
            mailto.append("?subject=${encodeURIComponent(subject)}")
            if (!body.isNullOrEmpty()) {
                mailto.append("&body=${encodeURIComponent(body)}")
            }
        } else if (!body.isNullOrEmpty()) {
            mailto.append("?body=${encodeURIComponent(body)}")
        }
        return generateQRCode(mailto.toString(), width, height)
    }

    fun generateWiFiQRCode(ssid: String, password: String, isHidden: Boolean, encryptionType: String, width: Int, height: Int): Bitmap? {
        val wifiConfig = "WIFI:T:$encryptionType;S:$ssid;P:$password;${if (isHidden) "H:true;" else ""};"
        return generateQRCode(wifiConfig, width, height)
    }

    fun generateGeoLocationQRCode(latitude: Double, longitude: Double, width: Int, height: Int): Bitmap? {
        val geoURI = "geo:$latitude,$longitude"
        return generateQRCode(geoURI, width, height)
    }

    fun generateContactInfoQRCode(contact: ContactInfo, width: Int, height: Int): Bitmap? {
        val vCard = """
            BEGIN:VCARD
            VERSION:3.0
            N:${contact.lastName};${contact.firstName}
            FN:${contact.firstName} ${contact.lastName}
            ORG:${contact.organization}
            TITLE:${contact.title}
            TEL;TYPE=WORK,VOICE:${contact.phone}
            EMAIL:${contact.email}
            ADR;TYPE=WORK:${contact.address}
            END:VCARD
        """.trimIndent()
        return generateQRCode(vCard, width, height)
    }

    private fun encodeURIComponent(s: String): String {
        return java.net.URLEncoder.encode(s, "UTF-8")
    }
}

data class ContactInfo(
    val firstName: String,
    val lastName: String,
    val organization: String,
    val title: String,
    val phone: String,
    val email: String,
    val address: String
)
