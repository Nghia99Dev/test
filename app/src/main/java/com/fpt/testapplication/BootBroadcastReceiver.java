package com.fpt.testapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i("BootBroadcastReceiver", "onReceive: " + "HAAAHHAAHHAHAHAHAHAHHAHAHAHAH");
        }
    }
}
