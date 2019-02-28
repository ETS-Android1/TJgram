package com.blikoon.qrcodescanner.camera

import android.hardware.Camera
import android.os.Handler

@Suppress("deprecation")
class PreviewCallback(
        private val configManager: CameraConfigurationManager
): Camera.PreviewCallback {

    private var previewHandler: Handler? = null
    private var previewMessage: Int = 0

    fun setHandler(previewHandler: Handler, previewMessage: Int) {
        this.previewHandler = previewHandler
        this.previewMessage = previewMessage
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        val cameraResolution = configManager.cameraResolution
        val message = previewHandler?.obtainMessage(previewMessage, cameraResolution?.width!!, cameraResolution.height, data)
        message?.sendToTarget()
        previewHandler = null
    }
}