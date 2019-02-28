package com.blikoon.qrcodescanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.text.TextUtils
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.blikoon.qrcodescanner.camera.CameraManager
import com.blikoon.qrcodescanner.decode.CaptureActivityHandler
import com.blikoon.qrcodescanner.decode.DecodeManager
import com.blikoon.qrcodescanner.decode.InactivityTimer
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_scan_qr.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.views.ViewUtil
import timber.log.Timber

@Suppress("deprecation")
class QrCodeActivity: AppCompatActivity(), Callback {

    companion object {
        const val EXTRA_QR_SCAN_RESULT = "result"

        const val REQUEST_PICTURE = 1
        const val VIBRATE_DURATION = 200L
    }

    private var mCaptureActivityHandler: CaptureActivityHandler? = null
    private var hasSurface: Boolean = false
    private var inactivityTimer: InactivityTimer? = null

    private val decodeManager = DecodeManager()

    private var mMediaPlayer: MediaPlayer? = null
    private var mPlayBeep: Boolean = false
    private var mVibrate: Boolean = false

    private var flashLightActive = false

    val captureActivityHandler: Handler?
        get() = mCaptureActivityHandler

    override fun setTheme(resId: Int) {
        super.setTheme(R.style.AppTheme_StatusBar_Transparent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_PICTURE) {
            finish()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)

        flashLayout.setOnClickListener {
            if (flashLightActive) {
                disableFlashLight()
            } else {
                enableFlashlight()
            }
        }

        if (Build.VERSION.SDK_INT >= 21) {
            flashIcon.setImageResource(R.drawable.asl_trimclip_flashlight)
        } else {
            flashIcon.setImageDrawable(ViewUtil.getIcon(this, R.drawable.ic_flash, R.color.md_white))
        }

        flashText.setText(R.string.enable_flash)

        hasSurface = false
        initData()
    }

    private fun initData() {
        CameraManager.init(this)
        inactivityTimer = InactivityTimer(this@QrCodeActivity)
    }

    override fun onResume() {
        super.onResume()
        val surfaceHolder = surfaceView.holder

        if (flashLightActive) {
            disableFlashLight()
        }

        if (hasSurface) {
            initCamera(surfaceHolder)
        } else {
            surfaceHolder.addCallback(this)
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }

        mPlayBeep = true
        val audioService = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false
        }

        mVibrate = true
    }

    override fun onPause() {
        super.onPause()
        if (mCaptureActivityHandler != null) {
            mCaptureActivityHandler!!.quitSynchronously()
            mCaptureActivityHandler = null
        }
        CameraManager.get()?.closeDriver()
    }

    override fun onDestroy() {
        if (null != inactivityTimer) {
            inactivityTimer!!.shutdown()
        }

        super.onDestroy()
    }

    fun handleDecode(result: Result?) {
        inactivityTimer?.onActivity()
        playBeepSoundAndVibrate()

        if (null == result) {
            decodeManager.showCouldNotReadQrCodeFromScanner(this, object: DecodeManager.OnRefreshCameraListener {
                override fun refresh() {
                    restartPreview()
                }
            })
        } else {
            handleResult(result.text)
        }
    }

    private fun initCamera(surfaceHolder: SurfaceHolder) {
        try {
            CameraManager.get()?.openDriver(surfaceHolder)
        } catch (re: RuntimeException) {
            Timber.e(re)
            return
        }

        qrFinderView.visibility = VISIBLE
        surfaceView.visibility = VISIBLE
        viewBackground.visibility = GONE

        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = CaptureActivityHandler(this)
        }
    }

    private fun restartPreview() {
        mCaptureActivityHandler?.restartPreviewAndDecode()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        hasSurface = false
    }

    private fun playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer?.start()
        }

        if (mVibrate) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VIBRATE_DURATION)
        }
    }

    private fun enableFlashlight() {
        flashText.setText(R.string.disable_flash)
        if (Build.VERSION.SDK_INT >= 21) {
            val stateSet = intArrayOf(android.R.attr.state_checked)
            flashIcon.setImageState(stateSet, true)
        } else {
            flashIcon.setImageDrawable(ViewUtil.getIcon(this, R.drawable.ic_flash, R.color.md_yellow_500))
        }

        flashLightActive = true
        CameraManager.get()?.setFlashLight(true)
    }

    private fun disableFlashLight() {
        flashText.setText(R.string.enable_flash)
        if (Build.VERSION.SDK_INT >= 21) {
            val stateSet = intArrayOf(android.R.attr.state_checked * -1)
            flashIcon.setImageState(stateSet, true)
        } else {
            flashIcon.setImageDrawable(ViewUtil.getIcon(this, R.drawable.ic_flash, R.color.md_white))
        }

        flashLightActive = false
        CameraManager.get()?.setFlashLight(false)
    }

    private fun handleResult(resultString: String) {
        if (TextUtils.isEmpty(resultString)) {
            decodeManager.showCouldNotReadQrCodeFromScanner(this, object: DecodeManager.OnRefreshCameraListener {
                override fun refresh() {
                    restartPreview()
                }
            })
        } else {
            val intent = Intent()
            intent.putExtra(EXTRA_QR_SCAN_RESULT, resultString)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}