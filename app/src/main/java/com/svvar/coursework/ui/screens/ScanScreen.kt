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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.svvar.coursework.model.ScannedData
import com.svvar.coursework.ui.components.AddContactDialog
import com.svvar.coursework.ui.components.CameraPreview
import com.svvar.coursework.ui.components.WiFiConnectDialog
import com.svvar.coursework.viewmodel.QRCodeViewModel
import com.svvar.qrcodegen.ContactInfo
import com.svvar.qrcodegen.QRCodeGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ScanQRCodeScreen(navController: NavController, viewModel: QRCodeViewModel = viewModel()) {
//    var scannedData by remember { mutableStateOf<ScannedData?>(null) }
//    var lastScannedData by remember { mutableStateOf<ScannedData?>(null) }
//    var isProcessing by remember { mutableStateOf(false) }
//    var showWiFiDialog by remember { mutableStateOf(false) }
//    var showAddContactDialog by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    val coroutineScope = rememberCoroutineScope()
//
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp) // Adjusted padding for better layout
//    ) {
//        Text(
//            text = "Scan QR Code",
//            style = MaterialTheme.typography.headlineMedium,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        val frameShape: RoundedCornerShape = RoundedCornerShape(16.dp)
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(1f) // Ensures the box is square
//                .padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            // Clip the CameraPreview to the frameShape
//            CameraPreview(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(frameShape),
//                onBarcodeScanned = { data ->
//                    if (!isProcessing && lastScannedData?.data != data.data) {
//                        isProcessing = true
//                        lastScannedData = data
//                        scannedData = data
//
//                        // Handle special cases as before
//                        when (data.type) {
//                            "Wi-Fi" -> showWiFiDialog = true
//                            "Contact Info" -> showAddContactDialog = true
//                            "URL" -> {
//                                // Open browser automatically
//                                data.data.let { url ->
//                                    if (url.isNotBlank()) {
//                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                                        try {
//                                            context.startActivity(intent)
//                                        } catch (e: Exception) {
//                                            // Optionally log the error
//                                        }
//                                    }
//                                }
//                            }
//                            "Geo-location" -> {
//                                // Open maps automatically
//                                val lat = data.barcode.geoPoint?.lat ?: 0.0
//                                val lng = data.barcode.geoPoint?.lng ?: 0.0
//                                val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
//                                val intent = Intent(Intent.ACTION_VIEW, uri)
//                                try {
//                                    context.startActivity(intent)
//                                } catch (e: Exception) {
//                                    // Optionally log the error
//                                }
//                            }
//                            "Email" -> {
//                                // Handle email
//                                data.barcode.email?.let { email ->
//                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
//                                        this.data = Uri.parse("mailto:${email.address}")
//                                        putExtra(Intent.EXTRA_SUBJECT, email.subject)
//                                        putExtra(Intent.EXTRA_TEXT, email.body)
//                                    }
//                                    try {
//                                        context.startActivity(intent)
//                                    } catch (e: Exception) {
//                                        // Optionally log the error
//                                    }
//                                }
//                            }
//                            else -> {
//                                // Handle plain text or other types
//                            }
//                        }
//
//                        // Reset the processing flag after a delay
//                        coroutineScope.launch {
//                            delay(2000) // 2-second delay; adjust as needed
//                            isProcessing = false
//                        }
//                    }
//                }
//            )
//
//
//            // Overlay frame
//            Box(
//                modifier = Modifier
//                    .matchParentSize()
//                    .border(
//                        width = 2.dp,
//                        color = MaterialTheme.colorScheme.primary,
//                        shape = frameShape
//                    )
//            )
//        }
//
//        Spacer(modifier = Modifier.height(32.dp)) // Increased spacing to move data lower
//
//        scannedData?.let { data ->
//            // Display scanned data
//            Text(
//                text = "Type: ${data.type}",
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//            Text(
//                text = "Data: ${data.data}",
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//        } ?: run {
//            Spacer(modifier = Modifier.height(16.dp)) // Added extra spacing
//            Text(
//                text = "No QR code scanned yet.",
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//        }
//    }
//
//
//    // Handle Saving QR Code
//    LaunchedEffect(scannedData) {
//        scannedData?.let { data ->
//            val generatedQRBitmap = when (data.type) {
//                "Plain Text" -> QRCodeGenerator.generatePlainTextQRCode(data.data, 512, 512)
//                "URL" -> QRCodeGenerator.generateURLQRCode(data.data, 512, 512)
//                "Email" -> {
//                    val email = data.barcode.email
//                    QRCodeGenerator.generateEmailQRCode(
//                        email?.address ?: "",
//                        email?.subject,
//                        email?.body,
//                        512,
//                        512
//                    )
//                }
//                "Wi-Fi" -> {
//                    val lines = data.data.split("\n")
//                    val ssid = lines.getOrNull(0)?.removePrefix("SSID: ")?.trim() ?: ""
//                    val password = lines.getOrNull(1)?.removePrefix("Password: ")?.trim() ?: ""
//                    val encryption = lines.getOrNull(2)?.removePrefix("Encryption: ")?.trim() ?: ""
//                    QRCodeGenerator.generateWiFiQRCode(ssid, password, false, encryption, 512, 512)
//                }
//                "Geo-location" -> {
//                    val geoPoint = data.barcode.geoPoint
//                    QRCodeGenerator.generateGeoLocationQRCode(
//                        latitude = geoPoint?.lat ?: 0.0,
//                        longitude = geoPoint?.lng ?: 0.0,
//                        width = 512,
//                        height = 512
//                    )
//                }
//                "Contact Info" -> {
//                    val contact = data.barcode.contactInfo
//                    val contactInfo = ContactInfo(
//                        firstName = contact?.name?.first ?: "",
//                        lastName = contact?.name?.last ?: "",
//                        organization = contact?.organization ?: "",
//                        title = contact?.title ?: "",
//                        phone = contact?.phones?.firstOrNull()?.number ?: "",
//                        email = contact?.emails?.firstOrNull()?.address ?: "",
//                        address = contact?.addresses?.firstOrNull()?.addressLines?.joinToString(", ") ?: ""
//                    )
//                    QRCodeGenerator.generateContactInfoQRCode(contactInfo, 512, 512)
//                }
//                else -> null
//            }
//
//            generatedQRBitmap?.let { bitmap ->
//                viewModel.saveQRCode(context, data.type, data.data, bitmap, "scan")
//            }
//
//            // Reset scannedData to prevent multiple saves
//            scannedData = null
//        }
//    }
//
//
//    if (showWiFiDialog && scannedData != null && scannedData!!.type == "Wi-Fi") {
//        val lines = scannedData!!.data.split("\n")
//        val ssid = if (lines.size > 0) lines[0].removePrefix("SSID: ").trim() else ""
//        val password = if (lines.size > 1) lines[1].removePrefix("Password: ").trim() else ""
//        val encryption = if (lines.size > 2) lines[2].removePrefix("Encryption: ").trim() else ""
//
//        WiFiConnectDialog(
//            ssid = ssid,
//            password = password,
//            encryptionType = encryption,
//            onConfirm = {
//                showWiFiDialog = false
//                // Launch Wi-Fi settings intent
//                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                try {
//                    context.startActivity(intent)
//                } catch (_: Exception) {
//                }
//            },
//            onDismiss = {
//                showWiFiDialog = false
//            }
//        )
//    }
//
//    if (showAddContactDialog && scannedData != null && scannedData!!.type == "Contact Info") {
//        val barcode = scannedData!!.barcode.contactInfo
//        val contactInfo = ContactInfo(
//            firstName = barcode?.name?.first ?: "",
//            lastName = barcode?.name?.last ?: "",
//            organization = barcode?.organization ?: "",
//            title = barcode?.title ?: "",
//            phone = barcode?.phones?.firstOrNull()?.number ?: "",
//            email = barcode?.emails?.firstOrNull()?.address ?: "",
//            address = barcode?.addresses?.firstOrNull()?.addressLines?.joinToString(", ") ?: ""
//        )
//
//        AddContactDialog(
//            contact = contactInfo,
//            onConfirm = {
//                showAddContactDialog = false
//                // Launch Add Contact intent
//                val intent = Intent(Intent.ACTION_INSERT).apply {
//                    type = ContactsContract.RawContacts.CONTENT_TYPE
//                    putExtra(ContactsContract.Intents.Insert.NAME, "${contactInfo.firstName} ${contactInfo.lastName}")
//                    putExtra(ContactsContract.Intents.Insert.COMPANY, contactInfo.organization)
//                    putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contactInfo.title)
//                    putExtra(ContactsContract.Intents.Insert.PHONE, contactInfo.phone)
//                    putExtra(ContactsContract.Intents.Insert.EMAIL, contactInfo.email)
//                    putExtra(ContactsContract.Intents.Insert.POSTAL, contactInfo.address)
//                }
//                try {
//                    context.startActivity(intent)
//                } catch (e: Exception) {
//                }
//            },
//            onDismiss = {
//                showAddContactDialog = false
//            }
//        )
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQRCodeScreen(navController: NavController, viewModel: QRCodeViewModel = viewModel()) {
    // State variables
    var scannedData by remember { mutableStateOf<ScannedData?>(null) }
    var lastScannedData by remember { mutableStateOf<ScannedData?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }
//    var currentDialogType by remember { mutableStateOf<String?>(null) }
    var showWiFiDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Adjusted padding for better layout
    ) {
        Text(
            text = "Scan QR Code",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val frameShape: RoundedCornerShape = RoundedCornerShape(16.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Ensures the box is square
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // CameraPreview composable with clipping and scanning control
            CameraPreview(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(frameShape),
                onBarcodeScanned = { data ->
                    // Only process scan if no dialog is open and the scan is new
                    if (!isDialogOpen && lastScannedData?.data != data.data) {
                        lastScannedData = data
                        scannedData = data
                        when (data.type) {
                            "Wi-Fi" -> showWiFiDialog = true
                            "Contact Info" -> showAddContactDialog = true
                            "URL" -> {
                                // Open browser automatically
                                data.data.let { url ->
                                    if (url.isNotBlank()) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Optionally log the error
                                        }
                                    }
                                }
                            }
                            "Geo-location" -> {
                                // Open maps automatically
                                val lat = data.barcode.geoPoint?.lat ?: 0.0
                                val lng = data.barcode.geoPoint?.lng ?: 0.0
                                val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Optionally log the error
                                }
                            }
                            "Email" -> {
                                // Handle email
                                data.barcode.email?.let { email ->
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        this.data = Uri.parse("mailto:${email.address}")
                                        putExtra(Intent.EXTRA_SUBJECT, email.subject)
                                        putExtra(Intent.EXTRA_TEXT, email.body)
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Optionally log the error
                                    }
                                }
                            }
                            else -> {
                                // Handle plain text or other types
                            }
                        }
                    }
                }
            )

            // Overlay frame
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = frameShape
                    )
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Increased spacing to move data lower

        scannedData?.let { data ->
            // Display scanned data
            Text(
                text = "Type: ${data.type}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "Data: ${data.data}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))
        } ?: run {
            Spacer(modifier = Modifier.height(16.dp)) // Added extra spacing
            Text(
                text = "No QR code scanned yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

        // Handle Saving QR Code
    LaunchedEffect(scannedData) {
        scannedData?.let { data ->
            val generatedQRBitmap = when (data.type) {
                "Plain Text" -> QRCodeGenerator.generatePlainTextQRCode(data.data, 512, 512)
                "URL" -> QRCodeGenerator.generateURLQRCode(data.data, 512, 512)
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
                "Geo-location" -> {
                    val geoPoint = data.barcode.geoPoint
                    QRCodeGenerator.generateGeoLocationQRCode(
                        latitude = geoPoint?.lat ?: 0.0,
                        longitude = geoPoint?.lng ?: 0.0,
                        width = 512,
                        height = 512
                    )
                }
                "Contact Info" -> {
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
                viewModel.saveQRCode(context, data.type, data.data, bitmap, "scan")
            }

            // Reset scannedData to prevent multiple saves
//            scannedData = null
        }
    }


    if (showWiFiDialog && scannedData != null && scannedData!!.type == "Wi-Fi") {
        val lines = scannedData!!.data.split("\n")
        val ssid = if (lines.size > 0) lines[0].removePrefix("SSID: ").trim() else ""
        val password = if (lines.size > 1) lines[1].removePrefix("Password: ").trim() else ""
        val encryption = if (lines.size > 2) lines[2].removePrefix("Encryption: ").trim() else ""

        WiFiConnectDialog(
            ssid = ssid,
            password = password,
            encryptionType = encryption,
            onConfirm = {
                showWiFiDialog = false
                isDialogOpen = false
                scannedData = null
                // Launch Wi-Fi settings intent
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(context, "Unable to open Wi-Fi settings", Toast.LENGTH_SHORT).show()

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
    if (showAddContactDialog && scannedData != null && scannedData!!.type == "Contact Info") {
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
                    Toast.makeText(context, "Unable to add contact", Toast.LENGTH_SHORT).show()
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
