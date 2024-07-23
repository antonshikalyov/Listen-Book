package com.example.listenbook.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.listenbook.R;

import java.util.ArrayList;
import java.util.List;

public class Permission {



    public void checkPermissions(Context context, int requestParameter) {
        List<String>  permissionsArray = new ArrayList<>();
        permissionsArray.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsArray.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsArray.add(Manifest.permission.FOREGROUND_SERVICE);
        permissionsArray.add(Manifest.permission.WAKE_LOCK);
        permissionsArray.add(Manifest.permission.MEDIA_CONTENT_CONTROL);
        permissionsArray.add(Manifest.permission.BROADCAST_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsArray.add(Manifest.permission.READ_MEDIA_AUDIO);
            permissionsArray.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsArray.add(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION);
            permissionsArray.add(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK);
        }



        String[] permissions = new String[permissionsArray.size()];
        permissionsArray.toArray(permissions);

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            Toast.makeText(context, "All permissions granted" + allPermissionsGranted, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "All permissions granted" + allPermissionsGranted, Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions((Activity) context, permissions, requestParameter);
        }
    }

    public void showSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.permission_dialog_title);
        builder.setMessage(R.string.permission_dialog_text);
        builder.setPositiveButton(R.string.sidebar_settings, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package",  context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }
}
