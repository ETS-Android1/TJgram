package org.michaelbel.tjgram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Toast;

import com.alexvasilkov.events.Events;
import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.animation.ViewPosition;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import org.michaelbel.tjgram.R;
import org.michaelbel.tjgram.ui.common.gestures.CircleGestureImageView;
import org.michaelbel.tjgram.utils.DeviceUtil;
import org.michaelbel.tjgram.utils.ViewUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import timber.log.Timber;

public class PhotoActivity extends AppCompatActivity {

    private static final String EXTRA_POSITION = "position";
    private static final String EXTRA_IMAGE_URL = "image_url";

    private Toolbar toolbar;

    private View background;
    private CircleGestureImageView photoView;

    public static void show(Activity fromActivity, ViewPosition position, String imageUrl) {
        Intent intent = new Intent(fromActivity, PhotoActivity.class);
        intent.putExtra(EXTRA_POSITION, position.pack());
        intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
        fromActivity.startActivity(intent);
        fromActivity.overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_save) {
            Toast.makeText(this, "Saving photo to downloads", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.item_share) {
            Toast.makeText(this, "Sharing photo", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Copy photo link", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ViewUtil.INSTANCE.getIcon(this, R.drawable.ic_arrow_back, R.color.primary));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.photo_of, 1, 1));
        }

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        params.topMargin = DeviceUtil.INSTANCE.statusBarHeight(this);

        ViewCompat.setElevation(appBarLayout, DeviceUtil.INSTANCE.dp(this, 0F));

        photoView = findViewById(R.id.fullImage);
        photoView.setOnClickListener(v -> {
            showSystemUi(isSystemUiShown());
            toolbar.setVisibility(isSystemUiShown() ? View.VISIBLE : View.INVISIBLE);
        });
        photoView.setOnDragListener((v, event) -> {
            Timber.e("onDrag");
            return false;
        });
        photoView.getController().addOnStateChangeListener(new GestureController.OnStateChangeListener() {
            @Override
            public void onStateChanged(State state) {
                Timber.e("onStateChanged");
            }

            @Override
            public void onStateReset(State oldState, State newState) {
                Timber.e("onStateReset");
            }
        });
        photoView.getController().setOnGesturesListener(new GestureController.OnGestureListener() {
            @Override
            public void onDown(@NonNull MotionEvent event) {
                Timber.e("onDown");
            }

            @Override
            public void onUpOrCancel(@NonNull MotionEvent event) {
                Timber.e("onUpOrCancel");
            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent event) {
                Timber.e("onSingleTapUp");
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
                Timber.e("onSingleTapConfirmed");
                return false;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent event) {
                Timber.e("onLongPress");
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent event) {
                Timber.e("onDoubleTap");
                return false;
            }
        });

        background = findViewById(R.id.backgroundView);

        photoView.setVisibility(View.INVISIBLE);
        background.setVisibility(View.INVISIBLE);

        String avatarUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        Picasso.get().load(avatarUrl).into(photoView);

        photoView.getPositionAnimator().addPositionUpdateListener(this::applyImageAnimationState);

        runAfterImageDraw(() -> {
            enterFullImage(savedInstanceState == null);
            Events.create(CrossEvents.SHOW_IMAGE).param(false).post();
        });
    }

    @Override
    public void onBackPressed() {
        if (!photoView.getPositionAnimator().isLeaving()) {
            photoView.getPositionAnimator().exit(true);
        }
    }

    private void enterFullImage(boolean animate) {
        ViewPosition position = ViewPosition.unpack(getIntent().getStringExtra(EXTRA_POSITION));
        photoView.getPositionAnimator().enter(position, animate);
    }

    private void applyImageAnimationState(float position, boolean isLeaving) {
        boolean isFinished = position == 0f && isLeaving; // Exit animation is finished

        background.setAlpha(position);
        background.setVisibility(isFinished ? View.INVISIBLE : View.VISIBLE);
        photoView.setVisibility(isFinished ? View.INVISIBLE : View.VISIBLE);

        if (isFinished) {
            Events.create(CrossEvents.SHOW_IMAGE).param(true).post();

            photoView.getController().getSettings().disableBounds();
            photoView.getPositionAnimator().setState(0f, false, false);

            runOnNextFrame(() -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void runAfterImageDraw(final Runnable action) {
        photoView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                runOnNextFrame(action);
                return true;
            }
        });
        photoView.invalidate();
    }

    private void runOnNextFrame(Runnable action) {
        final long frameLength = 17L;
        photoView.postDelayed(action, frameLength);
    }

    private boolean isSystemUiShown() {
        return (getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0;
    }

    private void showSystemUi(boolean show) {
        getWindow().getDecorView().setSystemUiVisibility(show ? 0 : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}