package org.michaelbel.tjgram.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

object FileUtil {

    fun getImageUri(inContext: Context, image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, image, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(context: Context, uri: Uri): String {
        var path = ""
        if (context.contentResolver != null) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    fun isImage(type: String): Boolean {
        return type == "jpg" || type == "JPG" ||
               type == "jpeg" || type == "JPEG" ||
               type == "png" || type == "bmp" || type == "webp"
    }

    fun isGif(type: String): Boolean {
        return type == "gif"
    }
}