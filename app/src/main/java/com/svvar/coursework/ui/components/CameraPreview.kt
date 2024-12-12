package com.svvar.coursework.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.svvar.coursework.model.ScannedData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onBarcodeScanned: (ScannedData) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    when {
        cameraPermissionState.hasPermission -> {
            CameraPreviewView(modifier, onBarcodeScanned)
        }
        cameraPermissionState.shouldShowRationale || !cameraPermissionState.permissionRequested -> {
            // Show a message explaining why the app needs camera permission
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                Text("Camera permission is required to scan QR codes.")
            }
        }
        else -> {
            // Permission permanently denied
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                Text("Camera permission was denied. Please enable it in settings.")
            }
        }
    }
}

@Composable
fun CameraPreviewView(
    modifier: Modifier = Modifier,
    onBarcodeScanned: (ScannedData) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalContext.current as androidx.lifecycle.LifecycleOwner)
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val preview = remember { Preview.Builder().build() }
    val barcodeAnalyzer = remember {
        BarcodeAnalyzer { result ->
            onBarcodeScanned(result)
        }
    }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(Executors.newSingleThreadExecutor(), barcodeAnalyzer)
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner.value,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    preview.setSurfaceProvider(surfaceProvider)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

