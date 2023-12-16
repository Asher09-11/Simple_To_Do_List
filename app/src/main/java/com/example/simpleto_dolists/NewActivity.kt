package com.example.simpleto_dolists

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.TimeZone

class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the AlarmManager
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Get the device's timezone offset
        val timeZoneOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis())

        // Adjust the interval based on the timezone offset
        val adjustedInterval = ALARM_INTERVAL + timeZoneOffset

        // Schedule the repeating alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            adjustedInterval.toLong(),
            pendingIntent
        )
    }

    companion object {
        private const val ALARM_INTERVAL = 60 * 60 * 1000 // 1 hour in milliseconds
    }
}
