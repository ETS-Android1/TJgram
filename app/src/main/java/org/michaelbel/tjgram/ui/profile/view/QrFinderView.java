package org.michaelbel.tjgram.ui.profile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.michaelbel.tjgram.R;
import org.michaelbel.tjgram.utils.DeviceUtil;

import androidx.core.content.ContextCompat;

public class QrFinderView extends RelativeLayout {

    private static final long ANIMATION_DELAY = 100L;

    private Paint mPaint;
    private int mMaskColor;
    private int mFrameColor;
    private Rect mFrameRect;
    private int mFocusThick;

    public QrFinderView(Context context) {
        this(context, null);
    }

    public QrFinderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QrFinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mMaskColor = ContextCompat.getColor(context, R.color.transparent50);
        mFrameColor = ContextCompat.getColor(context, R.color.foreground);
        mFocusThick = 2;
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }

        setWillNotDraw(false);
        LayoutInflater inflater = LayoutInflater.from(context);

        RelativeLayout parentLayout = (RelativeLayout) inflater.inflate(R.layout.layout_qr_scanner, this);
        FrameLayout frameLayout = parentLayout.findViewById(R.id.qrCodeScanner);

        mFrameRect = new Rect();
        LayoutParams layoutParams = (LayoutParams) frameLayout.getLayoutParams();
        mFrameRect.left = (DeviceUtil.INSTANCE.getScreenWidth(context) - layoutParams.width) / 2;
        mFrameRect.top = layoutParams.topMargin;
        mFrameRect.right = mFrameRect.left + layoutParams.width;
        mFrameRect.bottom = mFrameRect.top + layoutParams.height;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            return;
        }

        Rect frame = mFrameRect;
        if (frame == null) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        mPaint.setColor(mMaskColor);
        canvas.drawRect(0, 0, width, frame.top, mPaint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, mPaint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, mPaint);
        canvas.drawRect(0, frame.bottom + 1, width, height, mPaint);

        drawFocusRect(canvas, frame);
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }

    private void drawFocusRect(Canvas canvas, Rect rect) {
        mPaint.setColor(mFrameColor);
        canvas.drawRect(rect.left, rect.top, rect.right, rect.top + mFocusThick, mPaint);
        canvas.drawRect(rect.left, rect.top, rect.left + mFocusThick, rect.bottom, mPaint);
        canvas.drawRect(rect.right - mFocusThick, rect.top, rect.right, rect.bottom, mPaint);
        canvas.drawRect(rect.left, rect.bottom - mFocusThick, rect.right, rect.bottom, mPaint);
    }
}