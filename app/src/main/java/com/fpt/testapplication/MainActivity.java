package com.fpt.testapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.ktsh.Shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.NetworkInterface;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private static int REQUEST_PERMISSIONS = 1;

    private long downloadId;

    private DownloadManager downloadManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadId) {
                Log.i("TAG", "onReceive: " + downloadId);
                Log.i("TAG", "getMimeTypeForDownloadedFile: " + downloadManager.getMimeTypeForDownloadedFile(downloadId));

                Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
                downloadIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                downloadIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                downloadIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadId), downloadManager.getMimeTypeForDownloadedFile(downloadId));
                downloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(downloadIntent);

//                installPackage(downloadManager.getUriForDownloadedFile(downloadId));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        findViewById(R.id.fab).setOnClickListener(v -> {
//            ((TextView)findViewById(R.id.tvCenter)).setText("version new");

            downloadAPK("http://10.86.80.174:8010/app/app-debug.apk");

//            try {
//                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                powerManager.reboot(null);
//            } catch (Exception e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }


//            requestMultiplePermissions();


//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);


            Log.d(TAG, "onCreate: " + this.getPackageName());
//
//            for (PackageInstaller.SessionInfo session : packageInstaller.getAllSessions()) {
//                Log.d("TAG", "isActive: " + session.isActive());
//                Log.d("TAG", "getInstallerPackageName: " + session.getInstallerPackageName());
//                Log.d("TAG", "getAppPackageName: " + session.getAppPackageName());
//                Log.d("TAG", "getAppLabel: " + session.getSessionId());
//            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void downloadAPK(String url) {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setMimeType("application/vnd.android.package-archive")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "test.apk")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadId = downloadManager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0) {
                boolean allPermissionsGranted = true;

                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted) {
                    Toast.makeText(this, "allPermissionsGranted: true", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(this, "allPermissionsGranted: false", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    private void requestMultiplePermissions() {
        String[] permissions = {
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                // 添加其他需要的权限
        };

        boolean allPermissionsGranted = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            // 如果有一个或多个权限没有被授予，向用户请求权限
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        } else {
            // 已经有权限，执行相关操作
        }
    }

    private void installPackage(Uri uri) {
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);

        PackageInstaller packageInstaller = getPackageManager().getPackageInstaller();

        try {
            int sessionID = packageInstaller.createSession(sessionParams);
            PackageInstaller.Session session = packageInstaller.openSession(sessionID);

            addApkToInstallSession(uri, session);

            // Create an install status receiver.
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction("PACKAGE_INSTALLED_ACTION");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            IntentSender statusReceiver = pendingIntent.getIntentSender();
            // Commit the session (this will start the installation workflow).
            session.commit(statusReceiver);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addApkToInstallSession(Uri uri, PackageInstaller.Session session)
            throws IOException {
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try (OutputStream packageInSession = session.openWrite("package", 0, -1);
             InputStream is = this.getContentResolver().openInputStream(uri)) {
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
        }
    }
}