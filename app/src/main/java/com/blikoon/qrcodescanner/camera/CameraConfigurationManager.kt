package com.blikoon.qrcodescanner.camera

import android.content.Context
import android.hardware.Camera
import org.michaelbel.tjgram.core.views.DeviceUtil
import timber.log.Timber
import java.util.*
import java.util.regex.Pattern

@Suppress("deprecation")
class CameraConfigurationManager(private val context: Context) {

    companion object {
        const val TEN_DESIRED_ZOOM = 10

        private val COMMA_PATTERN = Pattern.compile(",")

        private fun findBestMotZoomValue(stringValues: CharSequence, tenDesiredZoom: Int): Int {
            var tenBestValue = 0
            for (stringValue : String in COMMA_PATTERN.split(stringValues)) {
                stringValue === stringValue.trim { it <= ' ' }
                val value: Double
                try {
                    value = java.lang.Double.parseDouble(stringValue)
                } catch (nfe: NumberFormatException) {
                    Timber.e(nfe)
                    return tenDesiredZoom
                }

                val tenValue = (10.0 * value).toInt()
                if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                    tenBestValue = tenValue
                }
            }
            return tenBestValue
        }
    }

    var cameraResolution: Camera.Size? = null
    private var pictureResolution: Camera.Size? = null

    fun initFromCameraParameters(camera: Camera) {
        val parameters = camera.parameters
        cameraResolution = findCloselySize(DeviceUtil.getScreenWidth(context),
                DeviceUtil.getScreenHeight(context), parameters.supportedPreviewSizes)
        pictureResolution = findCloselySize(DeviceUtil.getScreenWidth(context),
                DeviceUtil.getScreenHeight(context), parameters.supportedPictureSizes)
    }

    fun setDesiredCameraParameters(camera: Camera) {
        val parameters = camera.parameters
        parameters.setPreviewSize(cameraResolution!!.width, cameraResolution!!.height)
        parameters.setPictureSize(pictureResolution!!.width, pictureResolution!!.height)
        setZoom(parameters)
        camera.setDisplayOrientation(90)
        camera.parameters = parameters
    }

    private fun setZoom(parameters: Camera.Parameters) {
        val zoomSupportedString = parameters.get("zoom-supported")
        if (zoomSupportedString != null && !java.lang.Boolean.parseBoolean(zoomSupportedString)) {
            return
        }

        var tenDesiredZoom = TEN_DESIRED_ZOOM

        val maxZoomString = parameters.get("max-zoom")
        if (maxZoomString != null) {
            try {
                val tenMaxZoom = (10.0 * java.lang.Double.parseDouble(maxZoomString)).toInt()
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom
                }
            } catch (nfe: NumberFormatException) {
                Timber.e(nfe)
            }
        }

        val takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max")
        if (takingPictureZoomMaxString != null) {
            try {
                val tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString)
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom
                }
            } catch (nfe: NumberFormatException) {
                Timber.e(nfe)
            }
        }

        val motZoomValuesString = parameters.get("mot-zoom-values")
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom)
        }

        val motZoomStepString = parameters.get("mot-zoom-step")
        if (motZoomStepString != null) {
            try {
                val motZoomStep = java.lang.Double.parseDouble(motZoomStepString.trim { it <= ' ' })
                val tenZoomStep = (10.0 * motZoomStep).toInt()
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep
                }
            } catch (nfe: NumberFormatException) {
                Timber.e(nfe)
            }
        }
    }

    private fun findCloselySize(surfaceWidth: Int, surfaceHeight: Int, preSizeList: List<Camera.Size>): Camera.Size {
        Collections.sort<Camera.Size>(preSizeList, SizeComparator(surfaceWidth, surfaceHeight))
        return preSizeList[0]
    }

    class SizeComparator internal constructor(width: Int, height: Int) : Comparator<Camera.Size> {

        private val width: Int
        private val height: Int
        private val ratio: Float

        init {
            if (width < height) {
                this.width = height
                this.height = width
            } else {
                this.width = width
                this.height = height
            }
            this.ratio = this.height.toFloat() / this.width
        }

        override fun compare(size1: Camera.Size, size2: Camera.Size): Int {
            val width1 = size1.width
            val height1 = size1.height
            val width2 = size2.width
            val height2 = size2.height

            val ratio1 = Math.abs(height1.toFloat() / width1 - ratio)
            val ratio2 = Math.abs(height2.toFloat() / width2 - ratio)
            val result = java.lang.Float.compare(ratio1, ratio2)

            return if (result != 0) {
                result
            } else {
                val minGap1 = Math.abs(width - width1) + Math.abs(height - height1)
                val minGap2 = Math.abs(width - width2) + Math.abs(height - height2)
                minGap1 - minGap2
            }
        }
    }
}