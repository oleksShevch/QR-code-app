package com.svvar.coursework.ui.screens

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svvar.coursework.viewmodel.QRCodeViewModel
import com.svvar.qrcodegen.QRCodeGenerator
import com.svvar.qrcodegen.ContactInfo
import kotlinx.coroutines.launch
import java.io.OutputStream


fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val filename = "IMG_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/YourAppName")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        } else {
            @Suppress("DEPRECATION")
            put(MediaStore.Images.Media.DATA, "${android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)}/YourAppName/$filename")
        }
    }

    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
        }

        Toast.makeText(context, "Збережено до галереї", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Помилка зберігання", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQRCodeScreen(viewModel: QRCodeViewModel = viewModel()) {
    var selectedType by remember { mutableStateOf("Текст") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()


    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") } // Plain Text
    var urlText by remember { mutableStateOf("") } // URL
    var emailAddress by remember { mutableStateOf("") } // Email
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }
    var ssid by remember { mutableStateOf("") } // Wi-Fi
    var wifiPassword by remember { mutableStateOf("") }
    var encryptionType by remember { mutableStateOf("WPA") }
    var isHidden by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf("") } // Geo-location
    var longitude by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") } // Contact Info
    var lastName by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var encryptionExpanded by remember { mutableStateOf(false) }

    val qrTypes = listOf("Текст", "Посилання", "Email", "Wi-Fi", "Геолокація", "Контакт")
//    val qrTypes = listOf("Plain Text", "URL", "Email", "Wi-Fi", "Geo-location", "Contact Info")
//    val qrTypes = QRType.values().to
    val encryptionTypes = listOf("WEP", "WPA", "WPA2")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        item {
            Text(
                text = "Генерувати QR код",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        item {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    label = { Text("Тип QR коду") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    qrTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(text=type, style = MaterialTheme.typography.titleSmall) },
                            onClick = {
                                selectedType = type
                                expanded = false
                                qrBitmap = null
                                errorMessage = null
                            }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            when (selectedType) {
                "Текст" -> {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Введіть текст") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                "Посилання" -> {
                    OutlinedTextField(
                        value = urlText,
                        onValueChange = { urlText = it },
                        label = { Text("Введіть посилання") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri
                        )
                    )
                }

                "Email" -> {
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text("Електронна пошта") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                        )
                    )
                    OutlinedTextField(
                        value = emailSubject,
                        onValueChange = { emailSubject = it },
                        label = { Text("Тема (опціонально)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = emailBody,
                        onValueChange = { emailBody = it },
                        label = { Text("Повідомлення (опціонально)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )
                }

                "Wi-Fi" -> {
                    OutlinedTextField(
                        value = ssid,
                        onValueChange = { ssid = it },
                        label = { Text("SSID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = wifiPassword,
                        onValueChange = { wifiPassword = it },
                        label = { Text("Пароль") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = encryptionExpanded,
                        onExpandedChange = { encryptionExpanded = !encryptionExpanded }
                    ) {
                        OutlinedTextField(
                            value = encryptionType,
                            onValueChange = {},
                            label = { Text("Тип шифрування") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = encryptionExpanded)
                            },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = encryptionExpanded,
                            onDismissRequest = { encryptionExpanded = false }
                        ) {
                            encryptionTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(text=type, style = MaterialTheme.typography.titleSmall) },
                                    onClick = {
                                        encryptionType = type
                                        encryptionExpanded = false
                                        qrBitmap = null
                                        errorMessage = null
                                    }
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Checkbox(
                            checked = isHidden,
                            onCheckedChange = { isHidden = it }
                        )
                        Text(text = "Прихована мережа")
                    }
                }

                "Геолокація" -> {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Широта") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Довгота") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }

                "Контакт" -> {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("Ім'я") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Прізвище") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = organization,
                        onValueChange = { organization = it },
                        label = { Text("Організація") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Посада") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Номер телефону") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        )
                    )
                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text("Електронна пошта") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                        )
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Адреса") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Button(
                onClick = {
                    // Validate input and generate QR code

                    errorMessage = null
                    qrBitmap = when (selectedType) {
                        "Текст" -> {
                            if (inputText.isBlank()) {
                                errorMessage = "Введіть текст."
                                null
                            } else {
                                QRCodeGenerator.generatePlainTextQRCode(inputText, 512, 512)
                            }
                        }

                        "Посилання" -> {
                            if (urlText.isBlank()) {
                                errorMessage = "Введіть посилання."
                                null
                            } else {
                                QRCodeGenerator.generateURLQRCode(urlText, 512, 512)
                            }
                        }

                        "Email" -> {
                            if (emailAddress.isBlank()) {
                                errorMessage = "Введіть адресу електронної пошти."
                                null
                            }
                            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                                errorMessage = "Введіть коректну адресу електронної пошти."
                                null
                            }
                            else {
                                QRCodeGenerator.generateEmailQRCode(
                                    emailAddress,
                                    emailSubject.takeIf { it.isNotBlank() },
                                    emailBody.takeIf { it.isNotBlank() },
                                    512,
                                    512
                                )
                            }
                        }

                        "Wi-Fi" -> {
                            if (ssid.isBlank() || encryptionType.isBlank()) {
                                errorMessage = "Введіть SSID та тип шифрування."
                                null
                            } else {
                                QRCodeGenerator.generateWiFiQRCode(
                                    ssid = ssid,
                                    password = wifiPassword,
                                    isHidden = isHidden,
                                    encryptionType = encryptionType,
                                    width = 512,
                                    height = 512
                                )
                            }
                        }

                        "Геолокація" -> {
                            val lat = latitude.toDoubleOrNull()
                            val lon = longitude.toDoubleOrNull()
                            if (lat == null || lon == null) {
                                errorMessage = "Введіть коректні координати."
                                null
                            }
                            else if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                                errorMessage = "Координати повинні бути в межах:\nШирота: -90 до 90\nДовгота: -180 до 180"
                                null
                            }
                            else {
                                QRCodeGenerator.generateGeoLocationQRCode(lat, lon, 512, 512)
                            }
                        }

                        "Контакт" -> {
                            if (firstName.isBlank() && lastName.isBlank()) {
                                errorMessage = "Введіть необхідні контактні дані."
                                null
                            } else {
                                val contact = ContactInfo(
                                    firstName = firstName,
                                    lastName = lastName,
                                    organization = organization,
                                    title = title,
                                    phone = phone,
                                    email = contactEmail,
                                    address = address
                                )
                                QRCodeGenerator.generateContactInfoQRCode(contact, 512, 512)
                            }
                        }

                        else -> null
                    }

                    if (qrBitmap != null) {
                        coroutineScope.launch {
                            when (selectedType) {
                                "Текст" -> viewModel.saveQRCode(context, selectedType, inputText, qrBitmap!!, "Згенеровано")
                                "Посилання" -> viewModel.saveQRCode(context, selectedType, urlText, qrBitmap!!, "Згенеровано")
                                "Email" -> {
                                    val emailText = buildString {
                                        append("Адреса: $emailAddress\n")
                                        if (emailSubject.isNotBlank()) append("Тема: $emailSubject\n")
                                        if (emailBody.isNotBlank()) append("Текст: $emailBody")
                                    }
                                    viewModel.saveQRCode(context, selectedType, emailText, qrBitmap!!, "Згенеровано")
                                }
                                "Wi-Fi" -> {
                                    val wifiText = buildString {
                                        append("Назва: $ssid\n")
                                        append("Пароль: $wifiPassword\n")
                                        append("Шифрування: $encryptionType\n")
                                        append("Прихована мережа: ${if (isHidden) "Так" else "Ні"}")
                                    }
                                    viewModel.saveQRCode(context, selectedType, wifiText, qrBitmap!!, "Згенеровано")
                                }
                                "Геолокація" -> viewModel.saveQRCode(context, selectedType, "Широта: $latitude\nДовгота: $longitude", qrBitmap!!, "Згенеровано")
                                "Контакт" -> {
                                    val contactText = buildString {
                                        if (firstName.isNotBlank()) append("Ім'я: $firstName\n")
                                        if (lastName.isNotBlank()) append("Прізвище: $lastName\n")
                                        if (organization.isNotBlank()) append("Організація: $organization\n")
                                        if (title.isNotBlank()) append("Посада: $title\n")
                                        if (phone.isNotBlank()) append("Телефон: $phone\n")
                                        if (contactEmail.isNotBlank()) append("Email: $contactEmail\n")
                                        if (address.isNotBlank()) append("Адреса: $address")
                                    }
                                    viewModel.saveQRCode(context, selectedType, contactText, qrBitmap!!, "Згенеровано")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text="Генерувати", style = MaterialTheme.typography.headlineLarge)
            }
        }

        item {
            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            qrBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Generated QR Code",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }

        item {
            qrBitmap?.let { bitmap ->
                Button(
                    onClick = {
                        saveImageToGallery(context, bitmap)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Зберегти в галерею", style = MaterialTheme.typography.headlineLarge)
                }
            }
        }
    }
}
