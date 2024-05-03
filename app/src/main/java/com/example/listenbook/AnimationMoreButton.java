package com.example.listenbook;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class AnimationMoreButton {
    public void animationMoreBatton(LinearLayout layout) {
        int startColor = layout.getResources().getColor(android.R.color.background_light);
        int endColor = layout.getResources().getColor(android.R.color.transparent);
        ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(layout, "backgroundColor", endColor, startColor);
        colorAnimator.setDuration(700);
        colorAnimator.start();
        colorAnimator = ObjectAnimator.ofArgb(layout, "backgroundColor", startColor, endColor);
        colorAnimator.setDuration(700);
        colorAnimator.start();

    }

    public void animationMoreAboutSong(LinearLayout fullSongInformation, LinearLayout smallSongInformation, LinearLayout layout) {
        if (fullSongInformation.getVisibility() == View.VISIBLE) {
            smallSongInformation.setVisibility(View.VISIBLE);
            ValueAnimator animator = ValueAnimator.ofInt(fullSongInformation.getHeight(),fullSongInformation.getHeight()/2);
            animator.addUpdateListener(valueAnimator -> {
                int value = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = fullSongInformation.getLayoutParams();
                layoutParams.height = value;
                smallSongInformation.setLayoutParams(layoutParams);
            });
            animator.setDuration(800);
            animator.start();

            animationMoreBatton(layout);

            Animation slideUpAnimation = AnimationUtils.loadAnimation(layout.getContext(), R.anim.slide_up_left);
            smallSongInformation.startAnimation(slideUpAnimation);
            fullSongInformation.setVisibility(View.GONE);
        }  else {
            smallSongInformation.setVisibility(View.GONE);
            ValueAnimator animator = ValueAnimator.ofInt(smallSongInformation.getHeight(), fullSongInformation.getHeight());
            animator.addUpdateListener(valueAnimator -> {
                int value = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = fullSongInformation.getLayoutParams();
                layoutParams.height = value;
                fullSongInformation.setLayoutParams(layoutParams);
            });
            animator.setDuration(800);
            animator.start();

            Animation slideDownAnimation = AnimationUtils.loadAnimation(layout.getContext(), R.anim.slide_down_right);
            animationMoreBatton(layout);
            fullSongInformation.startAnimation(slideDownAnimation);
            fullSongInformation.setVisibility(View.VISIBLE);
        }
    }
    public void animationMoreAboutPlay(LinearLayout fullSongInformation, LinearLayout layout) {
        int x = fullSongInformation.getMeasuredHeight();
        if (fullSongInformation.getVisibility() == View.VISIBLE) {
            ValueAnimator animator = ValueAnimator.ofInt(fullSongInformation.getHeight(), 1);
            animator.addUpdateListener(valueAnimator -> {
                int value = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = fullSongInformation.getLayoutParams();
                layoutParams.height = value;
                fullSongInformation.setLayoutParams(layoutParams);
            });
            animator.setDuration(800);

            // Добавляем слушателя анимации
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Скрываем блок после завершения анимации
                    fullSongInformation.setVisibility(View.GONE);
                }
            });

            animator.start();

            animationMoreBatton(layout);

            Animation slideUpAnimation = AnimationUtils.loadAnimation(layout.getContext(), R.anim.slide_small_control_panel);
            fullSongInformation.startAnimation(slideUpAnimation);
        } else {
            fullSongInformation.setVisibility(View.VISIBLE);
            fullSongInformation.bringToFront();
            fullSongInformation.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            ValueAnimator animator = ValueAnimator.ofInt(0, fullSongInformation.getMeasuredHeight());
            animator.addUpdateListener(valueAnimator -> {
                int value = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = fullSongInformation.getLayoutParams();
                layoutParams.height = value;
                fullSongInformation.setLayoutParams(layoutParams);
            });
            animator.setDuration(800);
            animator.start();

            Animation slideDownAnimation = AnimationUtils.loadAnimation(layout.getContext(), R.anim.slide_down_right);
            fullSongInformation.startAnimation(slideDownAnimation);
            animationMoreBatton(layout);
        }

    }
}
