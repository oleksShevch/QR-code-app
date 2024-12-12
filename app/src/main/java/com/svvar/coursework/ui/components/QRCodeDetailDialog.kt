package com.svvar.coursework.ui.components

import android.R
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.svvar.coursework.data.QRCode
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun QRCodeDetailDialog(
    qrCode: QRCode,
    onDismiss: () -> Unit
) {
    // Format the timestamp to a readable date and time
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(qrCode.timestamp))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("QR Code Details") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Display the larger QR code image
                Image(
                    painter = rememberImagePainter(
                        data = "file://${qrCode.imagePath}",
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.ic_menu_report_image)
                            error(R.drawable.ic_menu_report_image)
                        }
                    ),
                    contentDescription = "QR Code Image",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Display QR code details
                Text(text = "Type: ${qrCode.type}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Content: ${qrCode.content}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Created At: $formattedDate", style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}