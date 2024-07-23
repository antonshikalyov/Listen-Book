package com.example.listenbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.PagerVH> {

    private final int[] colors = new int[]{
            R.drawable.slide1,
            R.drawable.slide2,
            R.drawable.slide3,
            R.drawable.slide4,
            0
    };

    private final int[] titleFirst = new int[]{
            R.string.titleFirstslideFirst,
            R.string.titleFirstslideSecond,
            R.string.titleFirstslideThird,
            R.string.titleFirstslideForth,
            0
    };
    private final int[] tittleSecond = new int[]{
            R.string.titleSecondslideFirst,
            R.string.titleSecondslideSecond,
            R.string.titleSecondslideThird,
            R.string.titleSecondslideForth,
            0
    };

    private final int[] content = new int[]{
            R.string.contentSlide1,
            R.string.contentSlide2,
            R.string.contentSlide3,
            R.string.contentSlide4,
            0
    };
    @NonNull
    @Override
    public PagerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_item, parent, false);



        return new PagerVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerVH holder, int position) {
        if (position < colors.length - 1) {
            holder.sliders.setVisibility(View.VISIBLE);
            holder.finalSlide.setVisibility(View.GONE);
            holder.tvAbout1.setText(titleFirst[position]);
            holder.tvAbout2.setText(tittleSecond[position]);
            holder.tvAbout3.setText(content[position]);
            holder.slide_image.setImageResource(colors[position]);
        } else {
            holder.sliders.setVisibility(View.GONE);
            holder.finalSlide.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    static class PagerVH extends RecyclerView.ViewHolder {
        TextView tvAbout1;
        TextView tvAbout2;
        TextView tvAbout3;
        ImageView slide_image;
        LinearLayout sliders;
        LinearLayout finalSlide;
        Button startButton;

        PagerVH(@NonNull View itemView) {
            super(itemView);
            tvAbout1 = itemView.findViewById(R.id.tvAbout1);
            tvAbout2 = itemView.findViewById(R.id.tvAbout2);
            tvAbout3 = itemView.findViewById(R.id.tvAbout3);
            slide_image = itemView.findViewById(R.id.slide_image);
            finalSlide = itemView.findViewById(R.id.final_slide);
            sliders = itemView.findViewById(R.id.just_sliders);
            startButton = itemView.findViewById(R.id.start_button);
        }
    }
}
