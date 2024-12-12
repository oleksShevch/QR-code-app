package com.svvar.coursework.ui.components


import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WiFiConnectDialog(
    ssid: String,
    password: String,
    encryptionType: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect to Wi-Fi") },
        text = {
            Column {
                Text("SSID: $ssid")
                Text("Password: $password")
                Text("Encryption: $encryptionType")
                Spacer(modifier = Modifier.height(16.dp))
                Text("To connect, please go to Wi-Fi settings.")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()

                }
            ) {
                Text("Open settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}