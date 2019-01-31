package com.c323proj9.jillpena;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    EditText reminderMessageEditText;
    EditText timeEditText;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    String reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reminderMessageEditText = findViewById(R.id.editTextReminderMessage);
        timeEditText = findViewById(R.id.editTextTime);
    }

    public void onClickSetAlarm(View view) {
        String time = timeEditText.getText().toString();
        reminder = reminderMessageEditText.getText().toString();
        Log.i("PROJ9", time);
        try {
            String[] hourMinutes = time.split(":");
            String[] minutesAMPM = hourMinutes[1].split(" ");
            if (hourMinutes[0] != null && hourMinutes[1] != null && minutesAMPM[1] != null) {
                Log.i("PROJ9", "TIME: " + Integer.parseInt(hourMinutes[0]) + ":" + hourMinutes[1] + " " + minutesAMPM[1]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR, Integer.parseInt(hourMinutes[0]));
                //Toast.makeText(this, "ALARM WILL GO OFF ON " + calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
                Log.i("PROJ9", "ALARM WILL GO OFF ON HOUR" + calendar.getTime().toString());
                calendar.set(Calendar.MINUTE, Integer.parseInt(minutesAMPM[0]));
                calendar.set(Calendar.SECOND, 00);
                if (minutesAMPM[1].equals("AM")) {
                    calendar.set(Calendar.AM_PM, Calendar.AM);
                } else {
                    calendar.set(Calendar.AM_PM, Calendar.PM);
                }
                Log.i("PROJ9", reminder + " \n" + "ALARM WILL GO OFF ON " + calendar.getTime().toString());
                alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmManagerBroadcastReceiver.class);
                intent.putExtra("MESSAGE", reminder);
                //sendBroadcast(intent);
                alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

                alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                Toast.makeText(this, "Alarm Set for " + calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
                Log.i("PROJ9", "ALARM Set for: " + calendar.getTime().toString());
                reminderMessageEditText.setText("");
                timeEditText.setText("");
            } else {
                Toast.makeText(this, "Please Provide a Correctly Fromatted Time: HH:mm AM/PM", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(this, "Please Provide a Correctly Fromatted Time: HH:mm AM/PM", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickStopAlarm(View view) {
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
            Intent intent = new Intent(this, Activity2.class);
            String reminder2 = reminder;
            intent.putExtra("REMINDER", reminder2);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "No Alarm to Stop", Toast.LENGTH_SHORT).show();
        }
    }


}
