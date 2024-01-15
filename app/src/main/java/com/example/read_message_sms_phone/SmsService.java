// SmsService.java
package com.example.read_message_sms_phone;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SmsService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static SmsService instance;

    // Singleton pattern: ensures only one instance of the service exists
    public static SmsService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Set the instance variable when the service is created
        instance = this;
        // Create a notification channel for Android Oreo and higher
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle the SMS when the service is started
        if (intent != null) {
            handleSms(intent);
        }
        // Indicate that the service should be restarted if it is killed by the system
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Return null as a service typically doesn't have a bound interface
        return null;
    }

    // Create a notification channel for Android Oreo and higher
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    // Show a notification when an SMS is received
    private void showNotification(String message) {
        // Create an intent to launch the MainActivity when the notification is clicked
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setAction("NOTIFICATION_CLICK_ACTION"); // Custom action to identify notification click

        // Create a PendingIntent for the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification using NotificationCompat
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SMS Received")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true) // This allows the notification to be cleared when clicked
                .setContentIntent(pendingIntent) // Set the pending intent
                .build();

        // Get the NotificationManager and notify with the created notification
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);

        // Start the service in the foreground with the created notification
        startForeground(NOTIFICATION_ID, notification);
    }

    // Handle the incoming SMS
    private void handleSms(Intent intent) {
        // Extract the SMS data from the intent's extras
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    // Create an SmsMessage from the PDU (protocol data unit)
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String messageBody = smsMessage.getMessageBody();
                    Log.d("SmsService", "Received SMS: " + messageBody);

                    // Show persistent notification for the received SMS
                    showNotification("Received SMS: " + messageBody);
                }
            }
        }
    }

    // Method to clear the notification and stop the foreground service
    public void clearNotification() {
        stopForeground(true);
    }
}
