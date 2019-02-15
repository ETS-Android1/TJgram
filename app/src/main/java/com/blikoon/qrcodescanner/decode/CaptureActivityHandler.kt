package com.blikoon.qrcodescanner.decode

import android.os.Handler
import android.os.Message

import com.google.zxing.Result
import org.michaelbel.tjgram.R

import com.blikoon.qrcodescanner.QrCodeActivity
import com.blikoon.qrcodescanner.camera.CameraManager
import timber.log.Timber

class CaptureActivityHandler(private val mActivity: QrCodeActivity) : Handler() {

    private val mDecodeThread: DecodeThread = DecodeThread(mActivity)
    private var mState: State? = null

    enum class State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    init {
        mDecodeThread.start()
        mState = State.SUCCESS
        restartPreviewAndDecode()
    }

    override fun handleMessage(message: Message) {
        if (message.what == R.id.auto_focus) {
            if (mState == State.PREVIEW) {
                CameraManager.get()?.requestAutoFocus(this, R.id.auto_focus)
            }
        } else if (message.what == R.id.decode_succeeded) {
            mState = State.SUCCESS
            mActivity.handleDecode(message.obj as Result)
        } else if (message.what == R.id.decode_failed) {
            mState = State.PREVIEW
            CameraManager.get()?.requestPreviewFrame(mDecodeThread.handler!!, R.id.decode)
        }
    }

    fun quitSynchronously() {
        mState = State.DONE
        CameraManager.get()!!.stopPreview()
        val quit = Message.obtain(mDecodeThread.handler, R.id.quit)
        quit.sendToTarget()

        try {
            mDecodeThread.join()
        } catch (e: InterruptedException) {
            Timber.e(e)
        }

        removeMessages(R.id.decode_succeeded)
        removeMessages(R.id.decode_failed)
    }

    fun restartPreviewAndDecode() {
        if (mState != State.PREVIEW) {
            CameraManager.get()?.startPreview()
            mState = State.PREVIEW
            CameraManager.get()?.requestPreviewFrame(mDecodeThread.handler!!, R.id.decode)
            CameraManager.get()?.requestAutoFocus(this, R.id.auto_focus)
        }
    }
}