package com.example.read_message_sms_phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


//NotificationListenerService this keyword for read or Receive message notification Other app if want example
//like facebook ,instagram, tiktok, ...............
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle incoming SMS here
        // Extract SMS data and send it to the server
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String messageBody = smsMessage.getMessageBody();
                    Log.d("SmsReceiver", "Received SMS: " + messageBody);

                    //show message Received
                    showToast(context, "Received SMS: " + messageBody);

                    // Start the SmsService to show a persistent notification
                    Intent serviceIntent = new Intent(context, SmsService.class);
                    serviceIntent.putExtras(bundle);
                    context.startService(serviceIntent);
                    if (context.startService(serviceIntent) != null) {
                        Log.d("SmsReceiver", "SmsService started successfully.");
                    } else {
                        Log.d("SmsReceiver", "Failed to start SmsService.");
                    }


                    // Send messageBody to the server
                    //sendToServer(messageBody);
                }
            }
        }
    }

    // Method to display a toast message
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Example using HttpURLConnection
    private void sendToServer(String messageBody) {
        try {
            // Replace "https://your-laravel-api-endpoint" with your actual Laravel API endpoint
            URL url = new URL("https://your-laravel-api-endpoint");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(messageBody.getBytes());
                out.flush();

                // Handle the server response if needed
                // You can read the response from the server here if necessary
                int responseCode = urlConnection.getResponseCode();
                Log.d("SmsReceiver", "Server response code: " + responseCode);
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
