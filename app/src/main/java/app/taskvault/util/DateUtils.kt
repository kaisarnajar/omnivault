package app.taskvault.util

import java.util.Calendar

object DateUtils {
    fun isToday(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
               now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    fun isThisWeek(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        // Set first day of week to Monday
        now.firstDayOfWeek = Calendar.MONDAY
        val date = Calendar.getInstance().apply { 
            timeInMillis = timestamp
            firstDayOfWeek = Calendar.MONDAY
        }
        return now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
               now.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR)
    }

    fun isThisMonth(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
               now.get(Calendar.MONTH) == date.get(Calendar.MONTH)
    }
}
