package com.blikoon.qrcodescanner.decode

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.blikoon.qrcodescanner.QrCodeActivity
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.michaelbel.tjgram.R
import timber.log.Timber
import java.util.*

class DecodeHandler(private val activity: QrCodeActivity): Handler() {

    private val qrCodeReader: QRCodeReader = QRCodeReader()
    private val hints: MutableMap<DecodeHintType, Any>
    private var rotatedData: ByteArray? = null

    init {
        hints = Hashtable()
        hints[DecodeHintType.CHARACTER_SET] = "utf-8"
        hints[DecodeHintType.TRY_HARDER] = java.lang.Boolean.TRUE
        hints[DecodeHintType.POSSIBLE_FORMATS] = BarcodeFormat.QR_CODE
    }

    override fun handleMessage(message: Message) {
        if (message.what == R.id.decode) {
            decode(message.obj as ByteArray, message.arg1, message.arg2)
        } else if (message.what == R.id.quit) {
            val looper = Looper.myLooper()
            looper?.quit()
        }
    }

    private fun decode(data: ByteArray, w: Int, h: Int) {
        var width = w
        var height = h

        if (null == rotatedData) {
            rotatedData = ByteArray(width * height)
        } else {
            if (rotatedData!!.size < width * height) {
                rotatedData = ByteArray(width * height)
            }
        }
        Arrays.fill(rotatedData!!, 0.toByte())
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (x + y * width >= data.size) {
                    break
                }
                rotatedData!![x * height + height - y - 1] = data[x + y * width]
            }
        }
        val tmp = width
        width = height
        height = tmp

        var rawResult: Result? = null
        try {
            val source = PlanarYUVLuminanceSource(rotatedData, width, height, 0, 0, width, height, false)
            val bitmap1 = BinaryBitmap(HybridBinarizer(source))
            rawResult = qrCodeReader.decode(bitmap1, hints)
        } catch (e: ReaderException) {
            Timber.e(e)
        } finally {
            qrCodeReader.reset()
        }

        if (rawResult != null) {
            val message = Message.obtain(activity.captureActivityHandler, R.id.decode_succeeded, rawResult)
            message.sendToTarget()
        } else {
            val message = Message.obtain(activity.captureActivityHandler, R.id.decode_failed)
            message.sendToTarget()
        }
    }
}
