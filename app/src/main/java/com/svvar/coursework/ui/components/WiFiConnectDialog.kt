package com.svvar.coursework.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WiFiConnectDialog(
    ssid: String,
    password: String,
//    encryptionType: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Підключення до WiFi", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Column {
                Text("Мережа: $ssid", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text("Пароль: $password", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Перейдіть в налаштування щоб підключитися", style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp).fillMaxWidth(), textAlign = TextAlign.Center)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()

                },
                modifier = Modifier.padding(start = 10.dp),
            ) {
                Text("Налаштування", color = Color(0xFF0070C0), style = MaterialTheme.typography.titleMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати", color = Color(0xFF0070C0), style = MaterialTheme.typography.titleMedium)
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color(0xFFE4F9FF),
    )
}