package com.blikoon.qrcodescanner.decode

import android.os.Handler
import android.os.Looper

import com.blikoon.qrcodescanner.QrCodeActivity
import timber.log.Timber

import java.util.concurrent.CountDownLatch

class DecodeThread(private val activity: QrCodeActivity): Thread() {

    private var mHandler: Handler? = null
    private val handlerInitLatch: CountDownLatch = CountDownLatch(1)

    val handler: Handler?
        get() {
            try {
                handlerInitLatch.await()
            } catch (ie: InterruptedException) {
                Timber.e(ie)
            }

            return mHandler
        }

    override fun run() {
        Looper.prepare()
        mHandler = DecodeHandler(activity)
        handlerInitLatch.countDown()
        Looper.loop()
    }
}