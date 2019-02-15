package com.blikoon.qrcodescanner.camera

import android.hardware.Camera
import android.os.Handler

class AutoFocusCallback : Camera.AutoFocusCallback {

    companion object {
        const val AUTO_FOCUS_INTERVAL_MS = 1500L
    }

    private var mAutoFocusMessage: Int = 0
    private var mAutoFocusHandler: Handler? = null

    override fun onAutoFocus(success: Boolean, camera: Camera) {
        if (mAutoFocusHandler != null) {
            val message = mAutoFocusHandler!!.obtainMessage(mAutoFocusMessage, success)
            mAutoFocusHandler!!.sendMessageDelayed(message, AUTO_FOCUS_INTERVAL_MS)
            mAutoFocusHandler = null
        }
    }

    fun setHandler(autoFocusHandler: Handler, autoFocusMessage: Int) {
        mAutoFocusHandler = autoFocusHandler
        mAutoFocusMessage = autoFocusMessage
    }
}