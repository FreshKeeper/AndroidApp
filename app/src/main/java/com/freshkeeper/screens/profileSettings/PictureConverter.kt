package com.freshkeeper.screens.profileSettings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

fun convertBitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun getBitmapFromUri(
    context: Context,
    uri: Uri,
): Bitmap? =
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        null
    }

fun convertBase64ToBitmap(base64String: String): Bitmap? =
    try {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
        Log.e("ImageError", "Fehler beim Dekodieren des Bildes: ${e.message}")
        null
    }

fun compressImage(
    uri: Uri,
    context: Context,
): Bitmap? {
    val originalBitmap = getBitmapFromUri(context, uri)
    return originalBitmap?.let {
        val scaledBitmap = Bitmap.createScaledBitmap(it, 500, 500, true)
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
