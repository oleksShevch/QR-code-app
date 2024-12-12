package com.svvar.coursework.model

import com.google.mlkit.vision.barcode.common.Barcode

data class ScannedData(
    val type: String,
    val data: String,
    val barcode: Barcode
)
