package com.blikoon.qrcodescanner.camera

import android.content.Context
import android.hardware.Camera
import android.os.Handler
import android.view.SurfaceHolder

import java.io.IOException

@Suppress("deprecation")
class CameraManager private constructor(context: Context) {

    companion object {
        private var sCameraManager: CameraManager? = null

        fun init(context: Context) {
            if (sCameraManager == null) {
                sCameraManager = CameraManager(context)
            }
        }

        fun get(): CameraManager? {
            return sCameraManager
        }
    }

    private val configManager: CameraConfigurationManager = CameraConfigurationManager(context)
    private var camera: Camera? = null
    private var initialized: Boolean = false
    private var previewing: Boolean = false

    private val previewCallback: PreviewCallback
    private val autoFocusCallback: AutoFocusCallback

    init {
        previewCallback = PreviewCallback(configManager)
        autoFocusCallback = AutoFocusCallback()
    }

    fun openDriver(holder: SurfaceHolder) {
        if (camera == null) {
            camera = Camera.open()
            if (camera == null) {
                throw IOException()
            }
            camera!!.setPreviewDisplay(holder)

            if (!initialized) {
                initialized = true
                configManager.initFromCameraParameters(camera!!)
            }
            configManager.setDesiredCameraParameters(camera!!)
        }
    }

    fun setFlashLight(open: Boolean): Boolean {
        if (camera == null) {
            return false
        }

        val parameters = camera!!.parameters ?: return false
        val flashModes = parameters.supportedFlashModes

        if (null == flashModes || 0 == flashModes.size) {
            return false
        }

        val flashMode = parameters.flashMode

        if (open) {
            if (Camera.Parameters.FLASH_MODE_TORCH == flashMode) {
                return true
            }

            return if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                camera!!.parameters = parameters
                true
            } else {
                false
            }
        } else {
            if (Camera.Parameters.FLASH_MODE_OFF == flashMode) {
                return true
            }

            return if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                camera!!.parameters = parameters
                true
            } else
                false
        }
    }

    fun closeDriver() {
        if (camera != null) {
            camera!!.release()
            initialized = false
            previewing = false
            camera = null
        }
    }

    fun startPreview() {
        if (camera != null && !previewing) {
            camera!!.startPreview()
            previewing = true
        }
    }

    fun stopPreview() {
        if (camera != null && previewing) {
            camera!!.stopPreview()
            previewing = false
        }
    }

    fun requestPreviewFrame(handler: Handler, message: Int) {
        if (camera != null && previewing) {
            previewCallback.setHandler(handler, message)
            camera!!.setOneShotPreviewCallback(previewCallback)
        }
    }

    fun requestAutoFocus(handler: Handler, message: Int) {
        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message)
            camera!!.autoFocus(autoFocusCallback)
        }
    }
}