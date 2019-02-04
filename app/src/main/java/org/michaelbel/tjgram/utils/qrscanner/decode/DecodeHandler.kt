package org.michaelbel.tjgram.utils.qrscanner.decode

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.ui.QrCodeActivity
import timber.log.Timber
import java.util.*

class DecodeHandler(private val mActivity: QrCodeActivity) : Handler() {

    private val mQrCodeReader: QRCodeReader = QRCodeReader()
    private val mHints: MutableMap<DecodeHintType, Any>
    private var mRotatedData: ByteArray? = null

    init {
        mHints = Hashtable()
        mHints[DecodeHintType.CHARACTER_SET] = "utf-8"
        mHints[DecodeHintType.TRY_HARDER] = java.lang.Boolean.TRUE
        mHints[DecodeHintType.POSSIBLE_FORMATS] = BarcodeFormat.QR_CODE
    }

    override fun handleMessage(message: Message) {
        if (message.what == R.id.decode) {
            decode(message.obj as ByteArray, message.arg1, message.arg2)
        } else if (message.what == R.id.quit) {
            val looper = Looper.myLooper()
            looper?.quit()
        }
    }

    private fun decode(data: ByteArray, width: Int, height: Int) {
        var width = width
        var height = height
        if (null == mRotatedData) {
            mRotatedData = ByteArray(width * height)
        } else {
            if (mRotatedData!!.size < width * height) {
                mRotatedData = ByteArray(width * height)
            }
        }
        Arrays.fill(mRotatedData!!, 0.toByte())
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (x + y * width >= data.size) {
                    break
                }
                mRotatedData!![x * height + height - y - 1] = data[x + y * width]
            }
        }
        val tmp = width
        width = height
        height = tmp

        var rawResult: Result? = null
        try {
            val source = PlanarYUVLuminanceSource(mRotatedData, width, height, 0, 0, width, height, false)
            val bitmap1 = BinaryBitmap(HybridBinarizer(source))
            rawResult = mQrCodeReader.decode(bitmap1, mHints)
        } catch (e: ReaderException) {
            Timber.e(e)
        } finally {
            mQrCodeReader.reset()
        }

        if (rawResult != null) {
            val message = Message.obtain(mActivity.captureActivityHandler, R.id.decode_succeeded, rawResult)
            message.sendToTarget()
        } else {
            val message = Message.obtain(mActivity.captureActivityHandler, R.id.decode_failed)
            message.sendToTarget()
        }
    }
}
