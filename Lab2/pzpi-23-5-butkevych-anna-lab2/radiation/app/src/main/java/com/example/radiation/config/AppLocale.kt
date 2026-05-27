package com.example.radiation.config

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object AppLocale {
    const val UKRAINIAN = "uk"
    const val ENGLISH = "en"
    const val DEFAULT = UKRAINIAN

    fun normalize(languageTag: String?): String {
        return when (languageTag) {
            UKRAINIAN, ENGLISH -> languageTag
            else -> DEFAULT
        }
    }

    fun apply(languageTag: String?) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(normalize(languageTag))
        )
    }
}

