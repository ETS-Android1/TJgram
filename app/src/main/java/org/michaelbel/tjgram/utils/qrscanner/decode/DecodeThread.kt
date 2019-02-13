package org.michaelbel.tjgram.utils.qrscanner.decode

import android.os.Handler
import android.os.Looper

import org.michaelbel.tjgram.ui.ScanQrActivity
import timber.log.Timber

import java.util.concurrent.CountDownLatch

class DecodeThread(private val mActivity: ScanQrActivity) : Thread() {

    private var mHandler: Handler? = null
    private val mHandlerInitLatch: CountDownLatch = CountDownLatch(1)

    val handler: Handler?
        get() {
            try {
                mHandlerInitLatch.await()
            } catch (ie: InterruptedException) {
                Timber.e(ie)
            }

            return mHandler
        }

    override fun run() {
        Looper.prepare()
        mHandler = DecodeHandler(mActivity)
        mHandlerInitLatch.countDown()
        Looper.loop()
    }
}