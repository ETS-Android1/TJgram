package com.blikoon.qrcodescanner.camera

import android.hardware.Camera
import android.os.Handler

class AutoFocusCallback: Camera.AutoFocusCallback {

    companion object {
        const val AUTO_FOCUS_INTERVAL_MS = 1500L
    }

    private var autoFocusMessage: Int = 0
    private var autoFocusHandler: Handler? = null

    override fun onAutoFocus(success: Boolean, camera: Camera) {
        val message = autoFocusHandler?.obtainMessage(autoFocusMessage, success)
        autoFocusHandler?.sendMessageDelayed(message, AUTO_FOCUS_INTERVAL_MS)
        autoFocusHandler = null
    }

    fun setHandler(autoFocusHandler: Handler, autoFocusMessage: Int) {
        this.autoFocusHandler = autoFocusHandler
        this.autoFocusMessage = autoFocusMessage
    }
}