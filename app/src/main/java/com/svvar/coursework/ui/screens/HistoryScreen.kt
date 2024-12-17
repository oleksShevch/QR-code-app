package com.svvar.coursework.ui.screens


import android.R
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.svvar.coursework.data.QRCode
import com.svvar.coursework.ui.components.QRCodeDetailDialog
import com.svvar.coursework.utils.RemoveWhiteBackground
import com.svvar.coursework.viewmodel.QRCodeViewModel
import java.util.Date
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: QRCodeViewModel = viewModel()
) {
    // Collect the list of QR codes as State
    val qrCodes by viewModel.allQRCodes.collectAsState()

    // State to track the selected QR code for the dialog
    var selectedQRCode by remember { mutableStateOf<QRCode?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Історія",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.displayLarge,
                    )
                },
                modifier = Modifier.padding(12.dp),
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (qrCodes.isEmpty()) {
                Text(
                    text = "Історія порожня",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(qrCodes) { qrCode ->
                        QRCodeItem(
                            qrCode = qrCode,
                            onClick = {
                                selectedQRCode = qrCode
                            }
                        )
                    }
                }
            }

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
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(qrCode.timestamp))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFC9E5FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = "file://${qrCode.imagePath}").apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                            placeholder(R.drawable.ic_menu_report_image)
                            error(R.drawable.ic_menu_report_image)
                        })
                        .transformations(RemoveWhiteBackground())
                        .build()
                ),
                contentDescription = "QR Code Image",
                modifier = Modifier
                    .size(96.dp)
                    .padding(end = 10.dp),
            )
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxHeight().padding(top = 4.dp)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) { // Display the action type and type
                    Text(
                        text = qrCode.type,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Column { // Display the action type and type
                        Text(
                            text = qrCode.actionType,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.headlineMedium
                        )

                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = qrCode.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewQRCodeItem() {
    QRCodeItem(
        qrCode = QRCode(
            id = 1,
            type = "Тип",
            actionType = "Дія",
            content = "Зміст",
            timestamp = System.currentTimeMillis(),
            imagePath = ""
        ),
        onClick = {}
    )
}