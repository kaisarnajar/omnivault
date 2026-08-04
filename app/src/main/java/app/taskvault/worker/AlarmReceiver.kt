package app.taskvault.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import app.taskvault.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val todoId = intent.getIntExtra("EXTRA_TODO_ID", 0)
        val title = intent.getStringExtra("EXTRA_TODO_TITLE") ?: "Task due soon!"

        showNotification(context, todoId, title)
    }

    private fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "taskvault_alarm_channel"

        // Use standard system alarm tone
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Task Alarms"
            val channel =
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Alarm notifications for tasks"

                    // Configure channel to play the alarm sound
                    val audioAttributes =
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    setSound(alarmSound, audioAttributes)

                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open the app when notification is tapped
        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                notificationId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val builder =
            NotificationCompat.Builder(context, channelId)
                // Need to use an existing icon. Since we don't know what's in drawable,
                // we can fallback to application info icon or android.R.drawable.ic_lock_idle_alarm
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("TaskVault Reminder")
                .setContentText("Your task '$title' is due in 30 minutes!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(alarmSound) // For pre-Oreo devices
                .setVibrate(longArrayOf(0, 1000, 500, 1000))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, builder.build())
    }
}
