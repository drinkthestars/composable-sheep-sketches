package com.canvas.sketch

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.PixelCopy
import android.view.Window
import androidx.annotation.RequiresApi
import java.io.OutputStreamx

// API Level 28
fun captureAndShare(
    width: Int,
    height: Int,
    rect: Rect,
    context: Context
) {
    val bitmap = Bitmap.createBitmap(
        /* width = */ width,
        /* height = */height,
        /* config = */Bitmap.Config.ARGB_8888
    )
    try {
        PixelCopy.request(
            /* source = */ context.getActivityWindow(),
            /* srcRect = */ Rect(rect),
            /* dest = */ bitmap,
            /* listener = */ { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    // share the bitmap
                    storeScreenShot(context, bitmap)
                } else {
                    Log.e("PixelCopy", "Failed to copy pixels: $copyResult")
                }
            },
            /* listenerThread = */ Handler(Looper.getMainLooper())
        )
    } catch (e: IllegalArgumentException) {
        Log.e("PixelCopy", "Failed to copy pixels: ${e.message}", e)
    }
}

// From https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/ui/ui-test/src/androidMain/kotlin/androidx/compose/ui/test/AndroidImageHelpers.android.kt;l=188
private fun Context.getActivityWindow(): Window {
    fun Context.getActivity(): Activity {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> this.baseContext.getActivity()
            else -> throw IllegalStateException(
                "Context is not an Activity context, but a ${javaClass.simpleName} context. " +
                        "An Activity context is required to get a Window instance"
            )
        }
    }
    return getActivity().window
}

fun storeScreenShot(context: Context, bitmap: Bitmap) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "image/jpeg"

    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, "title")
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    )

    try {
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                share.putExtra(Intent.EXTRA_STREAM, uri)
                context.startActivity(share)
            }
        }
    } catch (e: Exception) {
        Log.d("PixelCopy", "Error copying $uri", e)
    }
}
