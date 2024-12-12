package com.svvar.coursework.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        title = { Text("Add Contact") },
        text = {
            Column {
                Text("Name: ${contact.firstName} ${contact.lastName}")
                Text("Organization: ${contact.organization}")
                Text("Title: ${contact.title}")
                Text("Phone: ${contact.phone}")
                Text("Email: ${contact.email}")
                Text("Address: ${contact.address}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("To add this contact, please confirm.")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()

                }
            ) {
                Text("Add Contact")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}