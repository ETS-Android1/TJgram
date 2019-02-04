package org.michaelbel.tjgram.utils.qrscanner.camera

import android.hardware.Camera
import java.util.*

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