package org.michaelbel.tjgram.utils.qrscanner.camera

import android.content.Context
import android.hardware.Camera
import android.os.Handler
import android.view.SurfaceHolder

import java.io.IOException

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

    private val mConfigManager: CameraConfigurationManager = CameraConfigurationManager(context)
    private var mCamera: Camera? = null
    private var mInitialized: Boolean = false
    private var mPreviewing: Boolean = false

    private val mPreviewCallback: PreviewCallback
    private val mAutoFocusCallback: AutoFocusCallback

    init {
        mPreviewCallback = PreviewCallback(mConfigManager)
        mAutoFocusCallback = AutoFocusCallback()
    }

    fun openDriver(holder: SurfaceHolder) {
        if (mCamera == null) {
            mCamera = Camera.open()
            if (mCamera == null) {
                throw IOException()
            }
            mCamera!!.setPreviewDisplay(holder)

            if (!mInitialized) {
                mInitialized = true
                mConfigManager.initFromCameraParameters(mCamera!!)
            }
            mConfigManager.setDesiredCameraParameters(mCamera!!)
        }
    }

    fun setFlashLight(open: Boolean): Boolean {
        if (mCamera == null) {
            return false
        }

        val parameters = mCamera!!.parameters ?: return false
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
                mCamera!!.parameters = parameters
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
                mCamera!!.parameters = parameters
                true
            } else
                false
        }
    }

    fun closeDriver() {
        if (mCamera != null) {
            mCamera!!.release()
            mInitialized = false
            mPreviewing = false
            mCamera = null
        }
    }

    fun startPreview() {
        if (mCamera != null && !mPreviewing) {
            mCamera!!.startPreview()
            mPreviewing = true
        }
    }

    fun stopPreview() {
        if (mCamera != null && mPreviewing) {
            mCamera!!.stopPreview()
            mPreviewing = false
        }
    }

    fun requestPreviewFrame(handler: Handler, message: Int) {
        if (mCamera != null && mPreviewing) {
            mPreviewCallback.setHandler(handler, message)
            mCamera!!.setOneShotPreviewCallback(mPreviewCallback)
        }
    }

    fun requestAutoFocus(handler: Handler, message: Int) {
        if (mCamera != null && mPreviewing) {
            mAutoFocusCallback.setHandler(handler, message)
            mCamera!!.autoFocus(mAutoFocusCallback)
        }
    }
}