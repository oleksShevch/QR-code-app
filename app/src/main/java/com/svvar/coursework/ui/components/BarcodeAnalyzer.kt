package com.svvar.coursework.ui.components


import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.svvar.coursework.model.ScannedData
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val onBarcodeDetected: (ScannedData) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private var lastScannedData: String? = null
    private var lastScanTime = 0L


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue ?: continue
                        // Check if the same code was scanned recently
                        if (rawValue != lastScannedData || (currentTime - lastScanTime) > 2000) {
                            lastScannedData = rawValue
                            lastScanTime = currentTime
                            val data = parseBarcode(barcode)
                            onBarcodeDetected(data)
                        }
                        break
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("BarcodeAnalyzer", "Barcode scanning failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun parseBarcode(barcode: Barcode): ScannedData {
        val data = when (barcode.valueType) {
            Barcode.TYPE_WIFI -> {
                val ssid = barcode.wifi?.ssid ?: ""
                val password = barcode.wifi?.password ?: ""
                val encryptionType = when (barcode.wifi?.encryptionType) {
                    Barcode.WiFi.TYPE_OPEN -> "Open"
                    Barcode.WiFi.TYPE_WEP -> "WEP"
                    Barcode.WiFi.TYPE_WPA -> "WPA"
                    else -> "Unknown"
                }
                ScannedData(
                    type = "Wi-Fi",
                    data = "Назва: $ssid\nПароль: $password\nШифрування: $encryptionType",
                    barcode = barcode
                )
            }
            Barcode.TYPE_URL -> {
                val url = barcode.url?.url ?: ""
                ScannedData(
                    type = "Посилання",
//                    type = "URL",
                    data = url,
                    barcode = barcode
                )
            }
            Barcode.TYPE_CONTACT_INFO -> {
                val name = barcode.contactInfo?.name?.formattedName ?: ""
                val phone = barcode.contactInfo?.phones?.firstOrNull()?.number ?: ""
                val email = barcode.contactInfo?.emails?.firstOrNull()?.address ?: ""
                val address = barcode.contactInfo?.addresses?.firstOrNull()?.addressLines?.joinToString("\n") ?: ""
                val organization = barcode.contactInfo?.organization ?: ""
                val title = barcode.contactInfo?.title ?: ""
                ScannedData(
                    type = "Контакт",
                    data = {
                        val sb = StringBuilder()
                        if (name.isNotEmpty()) sb.append("Ім'я: $name\n")
                        if (phone.isNotEmpty()) sb.append("Тел.: $phone")
                        if (email.isNotEmpty()) sb.append("Email: $email\n")
                        if (address.isNotEmpty()) sb.append("Адреса: $address\n")
                        if (organization.isNotEmpty()) sb.append("Організація: $organization\n")
                        if (title.isNotEmpty()) sb.append("Посада: $title")
                        sb.toString()
                    }(),
                    barcode = barcode
                )
            }
            Barcode.TYPE_EMAIL -> {
                val address = barcode.email?.address ?: ""
                val subject = barcode.email?.subject ?: ""
                val body = barcode.email?.body ?: ""
                ScannedData(
                    type = "Email",
                    data = {
                        val sb = StringBuilder()
                        if (address.isNotEmpty()) sb.append("Ел. пошта: $address\n")
                        if (subject.isNotEmpty()) sb.append("Тема: $subject\n")
                        if (body.isNotEmpty()) sb.append("Повідомлення: $body")
                        sb.toString()
                    }(),
                    barcode = barcode
                )
            }
            Barcode.TYPE_GEO -> {
                val lat = barcode.geoPoint?.lat ?: 0.0
                val lng = barcode.geoPoint?.lng ?: 0.0
                ScannedData(
                    type = "Геолокація",
//                    type = "Geo-location",
                    data = "Широта: $lat\nДовгота: $lng",
                    barcode = barcode
                )
            }
            else -> {
                ScannedData(
                    type = "Текст",
//                    type = "Text",
                    data = barcode.rawValue ?: "",
                    barcode = barcode
                )
            }
        }
        return data
    }
}
