package com.svvar.coursework.ui.components

import android.R
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.svvar.coursework.data.QRCode
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun QRCodeDetailDialog(
    qrCode: QRCode,
    onDismiss: () -> Unit
) {
    // Format the timestamp to a readable date and time
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(qrCode.timestamp))
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE4F9FF),
        title = {
                Text(
                    "Деталі коду",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Display the larger QR code image
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = "file://${qrCode.imagePath}").apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                                placeholder(R.drawable.ic_menu_report_image)
                                error(R.drawable.ic_menu_report_image)
                            }).build()
                    ),
                    contentDescription = "QR Code Image",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Display QR code details
                Text(text = qrCode.type, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = qrCode.content, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "${qrCode.actionType}: $formattedDate", style = MaterialTheme.typography.titleSmall)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val imageFile = File(qrCode.imagePath)
                val qrCodeUri = FileProvider.getUriForFile(
                    context,
                    "com.svvar.coursework.fileprovider",
                    imageFile
                )
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, qrCodeUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "image/*"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Поділитися кодом"))
            },
                modifier = Modifier.padding(start = 50.dp),
            ) {
                Text("Поділитися", color = Color(0xFF0070C0), style = MaterialTheme.typography.titleMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрити", color = Color(0xFF0070C0),  style = MaterialTheme.typography.titleMedium)
            }
        }
    )
}