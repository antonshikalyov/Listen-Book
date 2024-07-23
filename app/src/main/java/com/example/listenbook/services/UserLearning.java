package com.example.listenbook.services;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageButton;

import com.example.listenbook.R;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.FullscreenPromptBackground;

public class UserLearning {
    public static void learningUserMain(Activity activity) {
        ImageButton toolbarLibraryButton = activity.findViewById(R.id.toolbar_library);
        ImageButton burgerButton = activity.findViewById(R.id.burger_button);

        toolbarLibraryButton.setEnabled(false);
        burgerButton.setEnabled(false);

        new MaterialTapTargetPrompt.Builder(activity)
                .setTarget(R.id.toolbar_library)
                .setPrimaryText(R.string.learningMainBooks)
                .setPrimaryTextColour(Color.WHITE)
                .setPromptBackground(new FullscreenPromptBackground())
                .setBackgroundColour(Color.argb(200, 0, 0, 0))
                .setFocalColour(Color.TRANSPARENT)
                .setIconDrawable(new ColorDrawable(Color.TRANSPARENT))
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                        new MaterialTapTargetPrompt.Builder(activity)
                                .setTarget(R.id.burger_button)
                                .setPrimaryText("Нажмите эту кнопку для открытия бокового меню")
                                .setPromptBackground(new FullscreenPromptBackground())
                                .setBackgroundColour(Color.argb(200, 0, 0, 0))
                                .setFocalColour(Color.TRANSPARENT)
                                .setIconDrawable(new ColorDrawable(Color.TRANSPARENT))
                                .setCaptureTouchEventOutsidePrompt(true)
                                .setPromptStateChangeListener((innerPrompt, innerState) -> {
                                    if (innerState == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || innerState == MaterialTapTargetPrompt.STATE_DISMISSED) {
                                        toolbarLibraryButton.setEnabled(true);
                                        burgerButton.setEnabled(true);
                                        SharedPreferences sharedPreferences = activity.getSharedPreferences("settings", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("MAIN_USER_LEARNING", 1);
                                        editor.apply();
                                    }
                                }).show();
                    }
                }).show();
    }

    public static int learningUserLibrary(Activity activity) {
        ImageButton add_book = activity.findViewById(R.id.add_book);

        add_book.setEnabled(false);

        new MaterialTapTargetPrompt.Builder(activity)
                .setTarget(R.id.add_book)
                .setPrimaryText(R.string.learningLibrary)
                .setPrimaryTextColour(Color.WHITE)
                .setPromptBackground(new FullscreenPromptBackground())
                .setBackgroundColour(Color.argb(200, 0, 0, 0))
                .setFocalColour(Color.TRANSPARENT)
                .setIconDrawable(new ColorDrawable(Color.TRANSPARENT))
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                        add_book.setEnabled(true);
                    }
                }).show();
        return 1;
    }
}
