package org.michaelbel.tjgram.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.google.zxing.Result;

import org.michaelbel.tjgram.R;
import org.michaelbel.tjgram.ui.profile.view.QrFinderView;
import org.michaelbel.tjgram.utils.ViewUtil;
import org.michaelbel.tjgram.utils.qrscanner.camera.CameraManager;
import org.michaelbel.tjgram.utils.qrscanner.decode.CaptureActivityHandler;
import org.michaelbel.tjgram.utils.qrscanner.decode.DecodeManager;
import org.michaelbel.tjgram.utils.qrscanner.decode.InactivityTimer;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import timber.log.Timber;

public class QrCodeActivity extends AppCompatActivity implements Callback {

    public static final int REQUEST_PICTURE = 1;
    public static final long VIBRATE_DURATION = 200L;
    public static final String QR_SCAN_RESULT = "result";

    private CaptureActivityHandler mCaptureActivityHandler;
    private boolean mHasSurface;
    private InactivityTimer mInactivityTimer;
    private QrFinderView mQrCodeFinderView;
    private SurfaceView mSurfaceView;

    private final DecodeManager mDecodeManager = new DecodeManager();

    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private boolean mVibrate;
    private boolean mNeedFlashLightOpen = true;

    private AppCompatImageView flashIcon;
    private AppCompatTextView flashText;

    @Override
    public void setTheme(int resId) {
        super.setTheme(R.style.AppTheme_StatusBar_Transparent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PICTURE) {
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        FrameLayout flashLayout = findViewById(R.id.flash_layout);
        flashLayout.setOnClickListener(v -> {
            if (mNeedFlashLightOpen) {
                turnFlashlightOn();
            } else {
                turnFlashLightOff();
            }
        });
        flashIcon = findViewById(R.id.flash_icon);
        flashIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(this, R.drawable.ic_flash, R.color.md_white));
        flashText = findViewById(R.id.flash_text);
        flashText.setText(R.string.enable_flash);

        initView();
        initData();
    }

    private void initView() {
        mQrCodeFinderView = findViewById(R.id.qr_code_view_finder);
        mSurfaceView = findViewById(R.id.qr_code_preview_view);
        mHasSurface = false;
    }

    private void initData() {
        CameraManager.Companion.init(this);
        mInactivityTimer = new InactivityTimer(QrCodeActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        turnFlashLightOff();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mPlayBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService != null && audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false;
        }
        initBeepSound();
        mVibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCaptureActivityHandler != null) {
            mCaptureActivityHandler.quitSynchronously();
            mCaptureActivityHandler = null;
        }
        Objects.requireNonNull(CameraManager.Companion.get()).closeDriver();
    }

    @Override
    protected void onDestroy() {
        if (null != mInactivityTimer) {
            mInactivityTimer.shutdown();
        }

        super.onDestroy();
    }

    public void handleDecode(Result result) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (null == result) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this, this::restartPreview);
        } else {
            String resultString = result.getText();

            handleResult(resultString);

        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            Objects.requireNonNull(CameraManager.Companion.get()).openDriver(surfaceHolder);
        } catch (RuntimeException re) {
            Timber.e(re);
            return;
        }

        mQrCodeFinderView.setVisibility(View.VISIBLE);
        mSurfaceView.setVisibility(View.VISIBLE);
        findViewById(R.id.view_background).setVisibility(View.GONE);

        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = new CaptureActivityHandler(this);
        }
    }

    private void restartPreview() {
        if (null != mCaptureActivityHandler) {
            mCaptureActivityHandler.restartPreviewAndDecode();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public Handler getCaptureActivityHandler() {
        return mCaptureActivityHandler;
    }

    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);
        }
    }

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VIBRATE_DURATION);
            }
        }
    }

    private final MediaPlayer.OnCompletionListener mBeepListener = mediaPlayer -> mediaPlayer.seekTo(0);

    private void turnFlashlightOn() {
        flashText.setText(R.string.disable_flash);
        flashIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(this, R.drawable.ic_flash, R.color.md_yellow_500));

        mNeedFlashLightOpen = false;
        Objects.requireNonNull(CameraManager.Companion.get()).setFlashLight(true);
    }

    private void turnFlashLightOff() {
        flashText.setText(R.string.enable_flash);
        flashIcon.setImageDrawable(ViewUtil.INSTANCE.getIcon(this, R.drawable.ic_flash, R.color.md_white));

        mNeedFlashLightOpen = true;
        Objects.requireNonNull(CameraManager.Companion.get()).setFlashLight(false);
    }

    private void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this, this::restartPreview);
        } else {
            Intent intent = new Intent();
            intent.putExtra(QR_SCAN_RESULT, resultString);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}