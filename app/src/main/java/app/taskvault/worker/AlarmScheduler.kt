package app.taskvault.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class AlarmScheduler @Inject constructor(@ApplicationContext private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(
        todoId: Int,
        title: String,
        timeInMillis: Long,
    ) {
        val intent =
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra("EXTRA_TODO_ID", todoId)
                putExtra("EXTRA_TODO_TITLE", title)
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                todoId, // Unique ID for this alarm
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent,
                    )
                } else {
                    // Fallback to inexact if permission is missing
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent,
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent,
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Fallback for missing permission
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent,
            )
        }
    }

    fun cancelAlarm(todoId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                todoId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        alarmManager.cancel(pendingIntent)
    }
}
