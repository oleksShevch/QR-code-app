package com.svvar.coursework.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.svvar.coursework.ui.theme.QRCodeAppTheme

@Composable
fun MainApp() {
    QRCodeAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MainNavigation()
        }
    }
}
