package com.svvar.coursework.ui.components

import androidx.compose.foundation.background
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

data class ContactInfo(
    val firstName: String,
    val lastName: String,
    val organization: String,
    val title: String,
    val phone: String,
    val email: String,
    val address: String
)

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
                Text("Ім'я: ${contact.firstName} ${contact.lastName}")
                Text("Організація: ${contact.organization}")
                Text("Підпис: ${contact.title}")
                Text("Телефон: ${contact.phone}")
                Text("Email: ${contact.email}")
                Text("Адреса: ${contact.address}")
                Spacer(modifier = Modifier.height(20.dp))
                Text("Підтвердіть щоб додати контакт", modifier = Modifier.padding(bottom = 16.dp, top = 16.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                },
                modifier = Modifier.padding(start = 90.dp),

            ) {
                Text("Додати", color = Color(0xFF0070C0), style = MaterialTheme.typography.titleSmall)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати", color = Color(0xFF0070C0), style = MaterialTheme.typography.titleSmall)
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
