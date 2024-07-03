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

    private final String[] str = new String[]{
            "Слушайте книги!",
            "Простое добавление",
            "Не тратьте время на",
            "Отдыхайте, учитесь и",
            ""
    };
    private final String[] str2 = new String[]{
            "Любые и где угодно!",
            "аудиокниг",
            "конвертацию аудиокниг",
            "развивайтесь с аудиокнигами",
            ""
    };

    private final String[] str3 = new String[]{
            "Слушайте аудиокниги скаченные из Интернета без ограничений. В любых аудиоформатах и независимо от наличия Интернета!",
            "Загружайте аудиокниги с компьютера через браузер, WebDav, из облачных сервисов и напрямую из приложения YouTube!",
            "Слушайте аудиокниги в любом из множеств поддерживаемых форматов аулиокниг: MP3, M4B, M4A, AWB, AAC, WMA, FLAC, OPUS, OGG. Также можно загружать аудилкниги в виде ZIP-архива.",
            "Умный таймер сна поможет тебе расслабиться. Закладки запомнят любимые места. Статистика посчитает сколько времени вы уделяете книгам",
            ""
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
            holder.tvAbout1.setText(str[position]);
            holder.tvAbout2.setText(str2[position]);
            holder.tvAbout3.setText(str3[position]);
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
