package com.example.radiation.extension

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension функції для роботи з датою та часом
 */

fun Date.format(pattern: String = "dd/MM/yyyy HH:mm"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun String.toDate(pattern: String = "dd/MM/yyyy HH:mm"): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

fun getCurrentDate(): Date {
    return Date(System.currentTimeMillis())
}

