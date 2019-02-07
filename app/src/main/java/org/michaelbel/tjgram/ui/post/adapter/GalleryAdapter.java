package org.michaelbel.tjgram.ui.post.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.tjgram.R;
import org.michaelbel.tjgram.utils.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private static final int VIEW_TYPE_MEDIA = 1;
    private static final int VIEW_TYPE_CAMERA = 2;
    private static final int VIEW_TYPE_GALLERY = 3;

    public interface PhotoClickListener {
        void onPhotoClick(File photo);
        void onCameraClick();
        void onGalleryClick();
    }

    private List<File> photos = new ArrayList<>();
    private PhotoClickListener photoClickListener;

    public void addListener(PhotoClickListener listener) {
        photoClickListener = listener;
    }

    public void swapData(List<File> list) {
        photos.addAll(list);
        photos.add(0, new File(""));
        photos.add(photos.size() - 1, new File(""));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photos != null ? photos.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_CAMERA;
        } else if (position == photos.size() - 1) {
            return VIEW_TYPE_GALLERY;
        } else {
          return VIEW_TYPE_MEDIA;
        }
    }

    @NotNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MEDIA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
            return new MediaViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false);
            return new BucketViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
        Uri data = Uri.fromFile(photos.get(position));

        if (getItemViewType(position) == VIEW_TYPE_CAMERA) {
            BucketViewHolder viewHolder = (BucketViewHolder) holder;
            viewHolder.mTextView.setText(R.string.item_camera);
            viewHolder.mImageView.setImageDrawable(ViewUtil.INSTANCE.getIcon(viewHolder.mImageView.getContext(), R.drawable.ic_camera, R.color.icon_active));
        } else if (getItemViewType(position) == VIEW_TYPE_GALLERY) {
            BucketViewHolder viewHolder = (BucketViewHolder) holder;
            viewHolder.mTextView.setText(R.string.item_gallery);
            viewHolder.mImageView.setImageDrawable(ViewUtil.INSTANCE.getIcon(viewHolder.mImageView.getContext(), R.drawable.ic_image_outline, R.color.icon_active));
        } else {
            MediaViewHolder viewHolder = (MediaViewHolder) holder;

            Picasso.get().load(data).resize(320, 320).centerCrop()
                .placeholder(R.drawable.placeholder_rectangle).error(R.drawable.error_rectangle).into(viewHolder.imageView);
        }
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;

        private ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    private class BucketViewHolder extends ViewHolder implements View.OnClickListener {

        private final TextView mTextView;
        private final AppCompatImageView mImageView;

        private BucketViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text);
            mImageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (photoClickListener != null) {
                    if (position == 0) {
                        photoClickListener.onCameraClick();
                    } else {
                        photoClickListener.onGalleryClick();
                    }
                }
            }
        }
    }

    private class MediaViewHolder extends ViewHolder implements View.OnClickListener {

        private MediaViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (photoClickListener != null) {
                    photoClickListener.onPhotoClick(photos.get(position));
                }
            }
        }
    }
}