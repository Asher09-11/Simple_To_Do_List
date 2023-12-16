package com.example.simpleto_dolists

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.simpleto_dolists.TaskDbHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = TaskDbHelper(context)

        dbHelper.deletePastTasks()
        val dueTasks = dbHelper.getDueTasks()

        // Use the result as needed, for example, show a notification
        for (task in dueTasks) {
            // Show notification or perform any other action for each due task
            showNotification(context, task)
        }
    }

    private fun showNotification(context: Context, task: Task) {
        // Create an explicit intent for the MainActivity
        val mainIntent = Intent(context, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0)

        // Format the due time for display in the notification
        val dueTimeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(task.dueTime))

        // Create a notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(task.title)
            .setContentText("Due at $dueTimeFormatted")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification channels are required for Android Oreo (API 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(task.id.toInt(), builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "your_channel_id"
    }
}
