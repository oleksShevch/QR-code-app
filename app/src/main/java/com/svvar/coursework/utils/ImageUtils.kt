package com.svvar.coursework.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    /**
     * Saves a bitmap image to internal storage and returns the file path.
     *
     * @param context The application context.
     * @param bitmap The Bitmap to save.
     * @param filename The desired filename for the image.
     * @return The absolute path to the saved image file.
     */
    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, filename: String): String {
        // Create a directory named "qr_codes" in internal storage
        val directory = File(context.filesDir, "qr_codes")
        if (!directory.exists()) {
            directory.mkdir()
        }

        // Create the image file within the directory
        val imageFile = File(directory, "$filename.png")
        FileOutputStream(imageFile).use { fos ->
            // Compress the bitmap and write it to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }

        return imageFile.absolutePath
    }

    /**
     * Loads a bitmap image from the given file path.
     *
     * @param path The absolute path to the image file.
     * @return The loaded Bitmap, or null if loading fails.
     */
    fun loadBitmapFromInternalStorage(path: String): Bitmap? {
        return BitmapFactory.decodeFile(path)
    }
}
