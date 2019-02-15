package com.blikoon.qrcodescanner.camera

import android.hardware.Camera
import android.os.Handler

class PreviewCallback(private val mConfigManager: CameraConfigurationManager) : Camera.PreviewCallback {

    private var mPreviewHandler: Handler? = null
    private var mPreviewMessage: Int = 0

    fun setHandler(previewHandler: Handler, previewMessage: Int) {
        mPreviewHandler = previewHandler
        mPreviewMessage = previewMessage
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        val cameraResolution = mConfigManager.cameraResolution

        if (mPreviewHandler != null) {
            val message = mPreviewHandler!!.obtainMessage(mPreviewMessage, cameraResolution?.width!!, cameraResolution.height, data)
            message.sendToTarget()
            mPreviewHandler = null
        }
    }
}