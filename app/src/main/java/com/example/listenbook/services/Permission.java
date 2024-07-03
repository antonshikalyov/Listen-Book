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
    private static final int REQUEST_CODE_PERMISSIONS = 123;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void checkPermissions(Context context) {
        setContext(context);
        List<String>  permissionsArray = new ArrayList<>();
        permissionsArray.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsArray.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsArray.add(Manifest.permission.FOREGROUND_SERVICE);
        permissionsArray.add(Manifest.permission.WAKE_LOCK);
        permissionsArray.add(Manifest.permission.MEDIA_CONTENT_CONTROL);
        permissionsArray.add(Manifest.permission.BROADCAST_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsArray.add(Manifest.permission.READ_MEDIA_AUDIO);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionsArray.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsArray.add(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsArray.add(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsArray.add(Manifest.permission.POST_NOTIFICATIONS);
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
            Toast.makeText(context, "All permissions granted", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, permissions, REQUEST_CODE_PERMISSIONS);
        }
    }

//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            boolean allPermissionsGranted = true;
//            for (int grantResult : grantResults) {
//                if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                    allPermissionsGranted = false;
//                    break;
//                }
//            }
//            if (allPermissionsGranted) {
//                Toast.makeText(context, "All permissions granted", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(context, "Some permissions denied", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null))
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//        }
//    }

    public void showSettingsDialog() {
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
