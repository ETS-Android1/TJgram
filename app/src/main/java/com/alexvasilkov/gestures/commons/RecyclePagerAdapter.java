package com.alexvasilkov.gestures.commons;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

/**
 * {@link PagerAdapter} implementation where each page is a regular view. Supports views recycling.
 * <p>
 * Inspired by {@link RecyclerView.Adapter}.
 */
public abstract class RecyclePagerAdapter<VH extends RecyclePagerAdapter.ViewHolder> extends PagerAdapter {

    private final Queue<VH> cache = new LinkedList<>();
    private final SparseArray<VH> attached = new SparseArray<>();

    public abstract VH onCreateViewHolder(@NonNull ViewGroup container);

    public abstract void onBindViewHolder(@NonNull VH holder, int position);

    public void onRecycleViewHolder(@NonNull VH holder) {
    }

    /**
     * Returns ViewHolder for given position if it exists within ViewPager, or null otherwise.
     *
     * @param position Item position
     * @return View holder for given position
     */
    public VH getViewHolder(int position) {
        return attached.get(position);
    }

    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        VH holder = cache.poll();
        if (holder == null) {
            holder = onCreateViewHolder(container);
        }
        attached.put(position, holder);

        // We should not use previous layout params, since ViewPager stores
        // important information there which cannot be reused
        container.addView(holder.itemView, null);

        onBindViewHolder(holder, position);
        return holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        VH holder = (VH) object;
        attached.remove(position);
        container.removeView(holder.itemView);
        cache.offer(holder);
        onRecycleViewHolder(holder);
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        ViewHolder holder = (ViewHolder) object;
        return holder.itemView == view;
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        // Forcing all views reinitialization when data set changed.
        // It should be safe because we're using views recycling logic.
        return POSITION_NONE;
    }

    public static class ViewHolder {
        public final View itemView;

        public ViewHolder(@NonNull View itemView) {
            this.itemView = itemView;
        }
    }
}