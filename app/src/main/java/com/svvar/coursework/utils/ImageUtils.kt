package com.svvar.coursework.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Color
import coil.size.Size
import coil.transform.Transformation


object ImageUtils {
    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, filename: String): String {
        // Create a directory named "qr_codes" in internal storage
        val directory = File(context.filesDir, "qr_codes")
        if (!directory.exists()) {
            directory.mkdir()
        }

        val imageFile = File(directory, "$filename.png")
        FileOutputStream(imageFile).use { fos ->
            // Compress the bitmap and write it to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }

        return imageFile.absolutePath
    }

    fun loadBitmapFromInternalStorage(path: String): Bitmap? {
        return BitmapFactory.decodeFile(path)
    }
}

class RemoveWhiteBackground: Transformation {
    override val cacheKey: String = "remove_white_background_transformation"

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap {
        // Create a mutable bitmap with the same dimensions
        val output = input.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until output.width) {
            for (y in 0 until output.height) {
                val pixel = output.getPixel(x, y)
                if (isWhite(pixel)) {
                    // Make the pixel transparent
                    output.setPixel(x, y, Color.TRANSPARENT)
                }
            }
        }

        return output
    }

    private fun isWhite(pixel: Int): Boolean {
        val threshold = 250 // Adjust as needed
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)
        return r > threshold && g > threshold && b > threshold
    }
}

