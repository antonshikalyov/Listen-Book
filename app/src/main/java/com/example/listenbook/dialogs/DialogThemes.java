package com.example.listenbook.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.listenbook.R;

public class DialogThemes extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(requireContext());
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.6f; // уровень затемнения от 1.0 до 0.0
        dialog.getWindow().setAttributes(lp);

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
// Установите заголовок
        dialog.setTitle("Заголовок диалога");
        dialog.setContentView(R.layout.dialog_themes);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();


        Drawable backgroundDrawable = ContextCompat.getDrawable(getContext(), R.drawable.radius_item_statistics);
        dialog.getWindow().setBackgroundDrawable(backgroundDrawable);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(params);




        return dialog;
    }
    public DialogInterface.OnClickListener zx () {
        return null;
    }

}