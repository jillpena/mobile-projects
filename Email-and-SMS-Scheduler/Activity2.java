package com.c323proj9.jillpena;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Activity2 extends AppCompatActivity {

    TextView yourMessage;
    EditText sendToEditText;
    EditText messageToBeSent;
    String TO;
    String message;
    int MY_SMS_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        yourMessage = findViewById(R.id.textViewYourMessage);
        sendToEditText = findViewById(R.id.editTextSendTo);
        messageToBeSent = findViewById(R.id.editTextMessageBody);
        Intent intent = getIntent();
        String reminderMessage = intent.getStringExtra("REMINDER");
        yourMessage.setText("Your Message: \n" + reminderMessage);
    }

    public void onClickSendEmail(View view) {
       Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        TO = sendToEditText.getText().toString();
        message = messageToBeSent.getText().toString();

        String mailTo = "mailto:" + TO + "?cc= " +
                "&subject= " + "&body=" + Uri.encode(message);
        emailIntent.setData(Uri.parse(mailTo));

        try {
            startActivity(emailIntent);
            finish();
            Log.i("PROJ9","Finished sending email... " + message);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Activity2.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

        sendToEditText.setText("");
        messageToBeSent.setText("");
    }

    public void onClickSendSMS(View view) {
        TO = sendToEditText.getText().toString();
        message = messageToBeSent.getText().toString();

        if (TO!=null) {
            String dial = "smsto:%s" + TO;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                            PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("SMS access needed");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setMessage("please confirm SMS access");
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                requestPermissions(
                                        new String[]{Manifest.permission.SEND_SMS}, MY_SMS_PERMISSIONS_REQUEST);
                            }
                        });
                        builder.show();
                    } else {
                        ActivityCompat.requestPermissions(Activity2.this,
                                new String[]{Manifest.permission.SEND_SMS}, MY_SMS_PERMISSIONS_REQUEST);
                    }
                } else {
                    try {
                        Toast.makeText(this, "Sending SMS...", Toast.LENGTH_LONG).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(dial, null, message, null, null);
                        Toast.makeText(this, "SMS sent.", Toast.LENGTH_LONG).show();
                        sendToEditText.setText("");
                        messageToBeSent.setText("");
                    }catch (Exception e){
                        Toast.makeText(this, "Can't send SMS", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                try {
                    Toast.makeText(this, "Sending SMS...", Toast.LENGTH_LONG).show();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(dial, null, message, null, null);
                    Toast.makeText(this, "SMS sent.", Toast.LENGTH_LONG).show();
                    sendToEditText.setText("");
                    messageToBeSent.setText("");
                }catch (Exception e){
                    Toast.makeText(this, "Can't send SMS", Toast.LENGTH_LONG).show();
                }
            }
        }
        else{
            Toast.makeText(this, "You Need to Provide a Phone Number", Toast.LENGTH_LONG).show();
        }


    }
}
