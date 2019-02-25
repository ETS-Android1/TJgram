package com.alexvasilkov.gestures.internal;

import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

public abstract class AnimationEngine implements Runnable {

    private static final long FRAME_TIME = 10L;

    private final View view;

    public AnimationEngine(@NonNull View view) {
        this.view = view;
    }

    @Override
    public final void run() {
        boolean continueAnimation = onStep();

        if (continueAnimation) {
            scheduleNextStep();
        }
    }

    public abstract boolean onStep();

    private void scheduleNextStep() {
        view.removeCallbacks(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.postOnAnimationDelayed(this, FRAME_TIME);
        } else {
            view.postDelayed(this, FRAME_TIME);
        }
    }

    public void start() {
        scheduleNextStep();
    }
}