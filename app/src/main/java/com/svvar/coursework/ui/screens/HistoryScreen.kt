package com.svvar.coursework.ui.screens


import android.R
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.svvar.coursework.data.QRCode
import com.svvar.coursework.ui.components.QRCodeDetailDialog
import com.svvar.coursework.viewmodel.QRCodeViewModel
import java.util.Date
import java.util.Locale

//@Composable
//fun HistoryScreen(navController: NavController) {
//    val historyItems = listOf(
//        "Scanned: https://openai.com",
//        "Generated: Wi-Fi Network",
//        "Scanned: Hello World",
//        // Add more dummy data
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp)
//    ) {
//        Text(
//            text = "History",
//            style = MaterialTheme.typography.displayLarge,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        LazyColumn {
//            items(historyItems) { item ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp),
//                    shape = MaterialTheme.shapes.medium,
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//                ) {
//                    Text(
//                        text = item,
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = MaterialTheme.colorScheme.onSurface,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: QRCodeViewModel = viewModel()
) {
    // Collect the list of QR codes as State
    val qrCodes by viewModel.allQRCodes.collectAsState()

    // State to track the selected QR code for the dialog
    var selectedQRCode by remember { mutableStateOf<QRCode?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Code History") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (qrCodes.isEmpty()) {
                // Display a message when there are no QR codes
                Text(
                    text = "No QR Codes Found.",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Display the list of QR codes in a LazyColumn
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(qrCodes) { qrCode ->
                        QRCodeItem(
                            qrCode = qrCode,
                            onDelete = {

                            },
                            onClick = {
                                selectedQRCode = qrCode // Set the selected QR code
                            }
                        )
                        Divider()
                    }
                }
            }

            // Show the dialog if a QR code is selected
            selectedQRCode?.let { qr ->
                QRCodeDetailDialog(
                    qrCode = qr,
                    onDismiss = { selectedQRCode = null }
                )
            }
        }
    }
}


@Composable
fun QRCodeItem(
    qrCode: QRCode,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    // Format the timestamp to a readable date and time
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(qrCode.timestamp))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }, // Handle item click
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Load the QR code image using Coil
            Image(
                painter = rememberImagePainter(
                    data = "file://${qrCode.imagePath}",
                    builder = {
                        crossfade(true)
                        placeholder(android.R.drawable.ic_menu_report_image)
                        error(android.R.drawable.ic_menu_report_image)
                    }
                ),
                contentDescription = "QR Code Image",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = qrCode.actionType,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = qrCode.type,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = qrCode.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Delete Icon Button
            IconButton(
                onClick = { onDelete() },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete QR Code"
                )
            }
        }
    }
}