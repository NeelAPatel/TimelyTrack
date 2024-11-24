package com.example.timelytrack.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toTimeWrapper(): TimeWrapper {
    return TimeWrapper(this)
}

class TimeWrapper(private val timestamp: Long) {
    private val date = Date(timestamp)

    // Get 12-hour format hour
    val get12Hour: Int
        get() = SimpleDateFormat("hh", Locale.getDefault()).format(date).toInt()

    // Get minute
    val getMinute: Int
        get() = SimpleDateFormat("mm", Locale.getDefault()).format(date).toInt()

    // Get AM/PM
    val getAMPM: String
        get() = SimpleDateFormat("a", Locale.getDefault()).format(date)

    // Get 24-hour format hour
    val get24Hour: Int
        get() = SimpleDateFormat("HH", Locale.getDefault()).format(date).toInt()

    // Get 12-hour formatted string time
    val get12StringTime: String
        get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)

    // Get 24-hour formatted string time
    val get24StringTime: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

    // Get custom formatted string time
    fun getFormattedTime(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
    fun getFormattedDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

}