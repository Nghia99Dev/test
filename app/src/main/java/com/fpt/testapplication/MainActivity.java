package com.fpt.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private long downloadId;

    private DownloadManager downloadManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id  = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadId) {
                Log.i("TAG", "onReceive: " + downloadId);
                Log.i("TAG", "getMimeTypeForDownloadedFile: " +  downloadManager.getMimeTypeForDownloadedFile(downloadId));

                Intent downloadIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                downloadIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                downloadIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                downloadIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadId), downloadManager.getMimeTypeForDownloadedFile(downloadId));
                downloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(downloadIntent);

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
//            downloadAPK("https://github.com/Nghia99Dev/test/raw/3ac6211b75448425fdd232ee516f48a343da681a/app/build/outputs/apk/debug/app-debug.apk");

            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            powerManager.reboot(null);
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
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadId = downloadManager.enqueue(request);
    }
}