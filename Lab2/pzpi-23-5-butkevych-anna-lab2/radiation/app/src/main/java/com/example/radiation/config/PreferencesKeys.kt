package com.example.radiation.config

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

/**
 * Ключі для збереження даних у DataStore Preferences
 */
object PreferencesKeys {
    // Auth
    val USER_TOKEN = stringPreferencesKey("user_token")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    
    // App settings
    val LANGUAGE = stringPreferencesKey("language")
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    
    // Location
    val LAST_LATITUDE = stringPreferencesKey("last_latitude")
    val LAST_LONGITUDE = stringPreferencesKey("last_longitude")
    
    // Other
    val LAST_SYNC = stringPreferencesKey("last_sync")
}

