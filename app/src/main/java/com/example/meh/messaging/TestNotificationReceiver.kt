package com.example.meh.messaging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TestNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Test Notification"
        val body = intent.getStringExtra("body") ?: "Firebase notification is working."
        NotificationHelper.sendNotification(context, title, body)
    }
}
