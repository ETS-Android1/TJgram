package com.blikoon.qrcodescanner.decode

import android.os.Handler
import android.os.Message
import com.blikoon.qrcodescanner.QrCodeActivity
import com.blikoon.qrcodescanner.camera.CameraManager
import com.google.zxing.Result
import org.michaelbel.tjgram.R
import timber.log.Timber

class CaptureActivityHandler(private val activity: QrCodeActivity): Handler() {

    private val decodeThread: DecodeThread = DecodeThread(activity)
    private var state: State? = null

    enum class State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    init {
        decodeThread.start()
        state = State.SUCCESS
        restartPreviewAndDecode()
    }

    override fun handleMessage(message: Message) {
        if (message.what == R.id.auto_focus) {
            if (state == State.PREVIEW) {
                CameraManager.get()?.requestAutoFocus(this, R.id.auto_focus)
            }
        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS
            activity.handleDecode(message.obj as Result)
        } else if (message.what == R.id.decode_failed) {
            state = State.PREVIEW
            CameraManager.get()?.requestPreviewFrame(decodeThread.handler!!, R.id.decode)
        }
    }

    fun quitSynchronously() {
        state = State.DONE
        CameraManager.get()!!.stopPreview()
        val quit = Message.obtain(decodeThread.handler, R.id.quit)
        quit.sendToTarget()

        try {
            decodeThread.join()
        } catch (e: InterruptedException) {
            Timber.e(e)
        }

        removeMessages(R.id.decode_succeeded)
        removeMessages(R.id.decode_failed)
    }

    fun restartPreviewAndDecode() {
        if (state != State.PREVIEW) {
            CameraManager.get()?.startPreview()
            state = State.PREVIEW
            CameraManager.get()?.requestPreviewFrame(decodeThread.handler!!, R.id.decode)
            CameraManager.get()?.requestAutoFocus(this, R.id.auto_focus)
        }
    }
}