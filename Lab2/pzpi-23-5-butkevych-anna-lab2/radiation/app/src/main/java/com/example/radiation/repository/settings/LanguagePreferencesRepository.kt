package com.example.radiation.repository.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.radiation.config.AppLocale
import com.example.radiation.config.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguagePreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val languageFlow: Flow<String> = dataStore.data
        .map { preferences -> AppLocale.normalize(preferences[PreferencesKeys.LANGUAGE]) }
        .distinctUntilChanged()

    suspend fun getLanguage(): String {
        val storedLanguage = dataStore.data.firstOrNull()?.get(PreferencesKeys.LANGUAGE)
        return AppLocale.normalize(storedLanguage)
    }

    suspend fun setLanguage(languageTag: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = AppLocale.normalize(languageTag)
        }
    }
}

