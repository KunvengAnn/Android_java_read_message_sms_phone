package com.example.read_message_sms_phone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SMS_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request SMS permissions if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS},
                    REQUEST_SMS_PERMISSIONS);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_SMS_PERMISSIONS) {
            // Check if the permissions were granted
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, you can proceed with your SMS-related functionality
            } else {
                // Permissions denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }



    //override when click on notification SmsService when click it close notification
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction() != null && intent.getAction().equals("NOTIFICATION_CLICK_ACTION")) {
            // Notification clicked, clear the notification
            SmsService.getInstance().clearNotification();
            Log.d("MainActivity", "SmsService Notification clicked!");
            Toast.makeText(this,"clicked",Toast.LENGTH_LONG).show();
        }
    }

}
