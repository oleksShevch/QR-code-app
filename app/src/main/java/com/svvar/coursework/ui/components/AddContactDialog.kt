package com.svvar.coursework.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.svvar.qrcodegen.ContactInfo

@Composable
fun AddContactDialog(
    contact: ContactInfo,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Додати контакт", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        },
        text = {
            Column {
                Text("Ім'я: ${contact.firstName} ${contact.lastName}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                if (contact.organization.isNotEmpty()){
                    Text("Організація: ${contact.organization}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                if (contact.title.isNotEmpty()) {
                    Text("Посада: ${contact.title}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                if (contact.phone.isNotEmpty()) {
                    Text("Телефон: ${contact.phone}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                if (contact.email.isNotEmpty()) {
                    Text("Email: ${contact.email}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                if (contact.address.isNotEmpty()) {
                    Text("Адреса: ${contact.address}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Підтвердіть щоб додати контакт", style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp).fillMaxWidth(), textAlign = TextAlign.Center)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                },
                modifier = Modifier.padding(start = 70.dp),

            ) {
                Text("Додати", color = Color(0xFF0070C0), style = MaterialTheme.typography.titleMedium)
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

@Preview
@Composable
fun PreviewDialog() {
    val contact = ContactInfo(
        "Іван",
        "Іванов",
        "Компанія",
        "Директор",
        "+380123456789",
        "aleee@gmail.com",
        "вул. Івана Іванова, 1"
    )
    AddContactDialog(contact, {}, {})
}
