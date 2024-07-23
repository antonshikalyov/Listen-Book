package com.example.listenbook.activities.play_track_activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.listenbook.R;
import com.example.listenbook.entities.AudioItem;
import com.example.listenbook.services.MediaPlaybackService;
import com.example.listenbook.services.Permission;
import com.example.listenbook.services.PermissionsCode;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder>  {

    private int activeItem = -2;
    private ArrayList<AudioItem> audioItems;
    private  LayoutInflater inflater;
    private  RecyclerView recyclerView;
    private Context context;
    private final Permission permission = new Permission();
    public static int deletingChapter;
    public static int currentPosition = -1;

    public PlayListAdapter(@NonNull Context context, ArrayList<AudioItem> audioItems, RecyclerView recyclerView) {
        this.audioItems = audioItems;
        this.inflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        this.context = context;
    }
    public PlayListAdapter() { }
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<AudioItem> newAudioItems) {
        this.audioItems = (ArrayList<AudioItem>) newAudioItems;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_chapter, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AudioItem item = audioItems.get(position);
        holder.bigNumber.setText(String.valueOf(item.id));
        holder.chapterTitle.setText(inflater.getContext().getString(R.string.chapter) + " " + item.title);
        holder.chapterDuration.setText(PlayPanelActivity.convertMils(Math.toIntExact(PlayPanelActivity.chapters.get(position).duration)));
        holder.seekBar.setVisibility(View.GONE);

        holder.bigNumber.setVisibility(View.VISIBLE);
        holder.playTrackBtn.setVisibility(View.GONE);
        holder.activeTrackAnimation.setVisibility(View.GONE);


        Log.i("getCurrentMediaItemIndex", String.valueOf(MediaPlaybackService.mediaController.getCurrentMediaItemIndex()));
        Log.i("currentPosition", String.valueOf(currentPosition));
        if (currentPosition == position && currentPosition-1 != MediaPlaybackService.mediaController.getCurrentMediaItemIndex()) {
            holder.bigNumber.setVisibility(View.VISIBLE);
            holder.playTrackBtn.setVisibility(View.GONE);
        }
        else if (currentPosition != position && activeItem == position && currentPosition-1 != MediaPlaybackService.mediaController.getCurrentMediaItemIndex()) {
             holder.bigNumber.setVisibility(View.GONE);
             holder.playTrackBtn.setVisibility(View.VISIBLE);
        }

        holder.playTrackBtn.setOnClickListener(view -> {
            if (MediaPlaybackService.mediaController.getCurrentMediaItemIndex() != position) {
                holder.playTrackBtn.setVisibility(View.GONE);
                MediaPlaybackService.mediaController.seekTo(position, 0L);
            }
        });

        holder.imageViewBtn.setOnClickListener(view -> {
            int previousActiveItem = activeItem;
            if (activeItem != position && MediaPlaybackService.mediaController.getCurrentMediaItemIndex() != position) {
                activeItem = position;
                notifyItemChanged(previousActiveItem);
                notifyItemChanged(activeItem);
            }
        });

        holder.trackListBtn.setOnClickListener(view -> showTrackDialog(position));
    }




    @Override
    public int getItemCount() {
        return audioItems.size();
    }

    public void updateSeekBarProgress(int position) {
        if (currentPosition != position) {
            ViewHolder viewHolder = getViewHolder(currentPosition);
            if (viewHolder != null) {
                viewHolder.seekBar.setVisibility(View.GONE);
                viewHolder.activeTrackAnimation.setVisibility(View.GONE);
                viewHolder.bigNumber.setVisibility(View.VISIBLE);
            }
            currentPosition = position;
        }

        ViewHolder viewHolder = getViewHolder(position);
        if (viewHolder != null) {
            if (MediaPlaybackService.mediaController.getCurrentMediaItemIndex() == position) {
                viewHolder.seekBar.setVisibility(View.VISIBLE);
                viewHolder.seekBar.setMin(0);
                viewHolder.seekBar.setMax((int) MediaPlaybackService.getMediaController().getDuration());
                viewHolder.seekBar.setProgress((int) MediaPlaybackService.getMediaController().getCurrentPosition());

                viewHolder.bigNumber.setVisibility(View.GONE);
                viewHolder.playTrackBtn.setVisibility(View.GONE);
                viewHolder.activeTrackAnimation.setVisibility(View.VISIBLE);
                viewHolder.activeTrackAnimation.setBackgroundResource(R.drawable.active_track_animation);
                AnimationDrawable animation = (AnimationDrawable) viewHolder.activeTrackAnimation.getBackground();

                if (animation != null) {
                    if (MediaPlaybackService.mediaController.isPlaying()) {
                        animation.start();
                    } else {
                        animation.stop();
                    }
                }
            } else {
                viewHolder.seekBar.setVisibility(View.GONE);
                viewHolder.activeTrackAnimation.setVisibility(View.GONE);
            }
        }
    }

    public void setCurrentItemIndex(int newIndex) {
        int oldIndex = MediaPlaybackService.mediaController.getPreviousMediaItemIndex();
        ViewHolder viewHolder = getViewHolder(newIndex);
        if (viewHolder != null) {
            viewHolder.trackListBtn.setVisibility(View.GONE);
        }
        notifyItemChanged(oldIndex);
        notifyItemChanged(newIndex);
    }

    @SuppressLint({"MissingInflatedId", "InlinedApi"})
    public void showTrackDialog(int pos) {
        deletingChapter = PlayPanelActivity.chapters.get(pos).id;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.NoBackgroundDialog);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customLayout = layoutInflater.inflate(R.layout.dialog_chapter, null);

        builder.setView(customLayout);

        TextView textView = customLayout.findViewById(R.id.play_track_item_title);
        textView.setText(audioItems.get(pos).title);
        AlertDialog dialog = builder.create();

        customLayout.findViewById(R.id.play_track_remove).setOnClickListener(view -> {
            deletingChapter = pos;
            permission.checkPermissions(context, PermissionsCode.REQUEST_CODE_PERMISSIONS_DELETE_CHAPTER);
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.play_track_item).setOnClickListener(view -> {
            MediaPlaybackService.mediaController.seekTo(pos, 0L);
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.play_track_cancel).setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private ViewHolder getViewHolder(int position) {
        if (recyclerView == null) {
            Log.e("PlayListAdapter", "RecyclerView is null");
            return null;
        }
        return (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView bigNumber;
        final TextView chapterTitle;
        final TextView chapterDuration;
        final ImageButton trackListBtn;
        final ImageButton playTrackBtn;
        final CardView imageViewBtn;
        final SeekBar seekBar;
        final ImageView activeTrackAnimation;

        ViewHolder(View view) {
            super(view);
            bigNumber = view.findViewById(R.id.big_number);
            chapterTitle = view.findViewById(R.id.chapter_title);
            chapterDuration = view.findViewById(R.id.chapter_duration);
            seekBar = view.findViewById(R.id.seekBarTrackList);
            trackListBtn = view.findViewById(R.id.track_list_btn);
            playTrackBtn = view.findViewById(R.id.track_play_btn);
            imageViewBtn = view.findViewById(R.id.image_view_btn);
            activeTrackAnimation = view.findViewById(R.id.active_track_animation);
        }
    }
}
