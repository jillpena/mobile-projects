package com.c323proj9.jillpena;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("MESSAGE");
        Toast.makeText(context, "REMINDER: \n" + message, Toast.LENGTH_LONG).show();
        Log.i("PROJ9", "ALARM GOING OFF!!!!!!!!!!");

    }
}
