package com.svvar.coursework.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svvar.coursework.model.ScannedData
import com.svvar.coursework.ui.components.AddContactDialog
import com.svvar.coursework.ui.components.CameraPreview
import com.svvar.coursework.ui.components.WiFiConnectDialog
import com.svvar.coursework.viewmodel.QRCodeViewModel
import com.svvar.qrcodegen.ContactInfo
import com.svvar.qrcodegen.QRCodeGenerator

@Composable
fun ScanQRCodeScreen(viewModel: QRCodeViewModel = viewModel()) {
    var scannedData by remember { mutableStateOf<ScannedData?>(null) }
    var lastScannedData by remember { mutableStateOf<ScannedData?>(null) }
    var lastScannedTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var showWiFiDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Сканувати QR код",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        val frameShape = RoundedCornerShape(16.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // CameraPreview composable with clipping and scanning control
            CameraPreview(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(frameShape),
                onBarcodeScanned = { data ->
                    if (!isDialogOpen && lastScannedData?.data != data.data || (System.currentTimeMillis() - lastScannedTime) > 3000) {
                        lastScannedData = data
                        scannedData = data
                        lastScannedTime = System.currentTimeMillis()
                        when (data.type) {
                            "Wi-Fi" -> showWiFiDialog = true
                            "Контакт" -> showAddContactDialog = true
                            "Посилання" -> {
                                data.data.let { url ->
                                    if (url.isNotBlank()) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                        }
                                    }
                                }
                            }
                            "Геолокація" -> {
                                val lat = data.barcode.geoPoint?.lat ?: 0.0
                                val lng = data.barcode.geoPoint?.lng ?: 0.0
                                val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                }
                            }
                            "Email" -> {
                                data.barcode.email?.let { email ->
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        this.data = Uri.parse("mailto:${email.address}")
                                        putExtra(Intent.EXTRA_SUBJECT, email.subject)
                                        putExtra(Intent.EXTRA_TEXT, email.body)
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = frameShape
                    )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        scannedData?.let { data ->
            Text(
                text = "Тип: ${data.type}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Дані: ${data.data}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))
        } ?: run {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "QR код не проскановано.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }

        // Handle Saving QR Code
    LaunchedEffect(scannedData) {
        scannedData?.let { data ->
            val generatedQRBitmap = when (data.type) {
                "Текст" -> QRCodeGenerator.generatePlainTextQRCode(data.data, 512, 512)
                "Посилання" -> QRCodeGenerator.generateURLQRCode(data.data, 512, 512)
                "Email" -> {
                    val email = data.barcode.email
                    QRCodeGenerator.generateEmailQRCode(
                        email?.address ?: "",
                        email?.subject,
                        email?.body,
                        512,
                        512
                    )
                }
                "Wi-Fi" -> {
                    val lines = data.data.split("\n")
                    val ssid = lines.getOrNull(0)?.removePrefix("SSID: ")?.trim() ?: ""
                    val password = lines.getOrNull(1)?.removePrefix("Password: ")?.trim() ?: ""
                    val encryption = lines.getOrNull(2)?.removePrefix("Encryption: ")?.trim() ?: ""
                    QRCodeGenerator.generateWiFiQRCode(ssid, password, false, encryption, 512, 512)
                }
                "Геолокація" -> {
                    val geoPoint = data.barcode.geoPoint
                    QRCodeGenerator.generateGeoLocationQRCode(
                        latitude = geoPoint?.lat ?: 0.0,
                        longitude = geoPoint?.lng ?: 0.0,
                        width = 512,
                        height = 512
                    )
                }
                "Контакт" -> {
                    val contact = data.barcode.contactInfo
                    val contactInfo = ContactInfo(
                        firstName = contact?.name?.first ?: "",
                        lastName = contact?.name?.last ?: "",
                        organization = contact?.organization ?: "",
                        title = contact?.title ?: "",
                        phone = contact?.phones?.firstOrNull()?.number ?: "",
                        email = contact?.emails?.firstOrNull()?.address ?: "",
                        address = contact?.addresses?.firstOrNull()?.addressLines?.joinToString(", ") ?: ""
                    )
                    QRCodeGenerator.generateContactInfoQRCode(contactInfo, 512, 512)
                }
                else -> null
            }

            generatedQRBitmap?.let { bitmap ->
                viewModel.saveQRCode(context, data.type, data.data, bitmap, "Скановано")
            }

        }
    }


    if (showWiFiDialog && scannedData != null && scannedData!!.type == "Wi-Fi") {
        val lines = scannedData!!.data.split("\n")
        val ssid = if (lines.size > 0) lines[0].removePrefix("Назва: ").trim() else ""
        val password = if (lines.size > 1) lines[1].removePrefix("Пароль: ").trim() else ""
        val encryption = if (lines.size > 2) lines[2].removePrefix("Шифрування: ").trim() else ""

        WiFiConnectDialog(
            ssid = ssid,
            password = password,
//            encryptionType = encryption,
            onConfirm = {
                showWiFiDialog = false
                isDialogOpen = false
                scannedData = null
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(context, "Не вдалося відкрити налаштування", Toast.LENGTH_SHORT).show()

                }
            },
            onDismiss = {
                showWiFiDialog = false
                isDialogOpen = false
                scannedData = null
            }
        )
    }


    // Handle Add Contact Dialog
    if (showAddContactDialog && scannedData != null && scannedData!!.type == "Контакт") {
        val barcode = scannedData!!.barcode.contactInfo
        val contactInfo = ContactInfo(
            firstName = barcode?.name?.first ?: "",
            lastName = barcode?.name?.last ?: "",
            organization = barcode?.organization ?: "",
            title = barcode?.title ?: "",
            phone = barcode?.phones?.firstOrNull()?.number ?: "",
            email = barcode?.emails?.firstOrNull()?.address ?: "",
            address = barcode?.addresses?.firstOrNull()?.addressLines?.joinToString(", ") ?: ""
        )

        AddContactDialog(
            contact = contactInfo,
            onConfirm = {
                // Handle Add Contact
                showAddContactDialog = false
                isDialogOpen = false
                scannedData = null

                // Launch Add Contact intent
                val intent = Intent(Intent.ACTION_INSERT).apply {
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                    putExtra(ContactsContract.Intents.Insert.NAME, "${contactInfo.firstName} ${contactInfo.lastName}")
                    putExtra(ContactsContract.Intents.Insert.COMPANY, contactInfo.organization)
                    putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contactInfo.title)
                    putExtra(ContactsContract.Intents.Insert.PHONE, contactInfo.phone)
                    putExtra(ContactsContract.Intents.Insert.EMAIL, contactInfo.email)
                    putExtra(ContactsContract.Intents.Insert.POSTAL, contactInfo.address)
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Не вдалося додати контакт", Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = {
                // Handle dialog dismiss
                showAddContactDialog = false
                isDialogOpen = false
                scannedData = null
            }
        )
    }
}
