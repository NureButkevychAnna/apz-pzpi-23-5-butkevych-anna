package com.example.radiation.network

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.radiation.config.PreferencesKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor для додавання JWT токена до кожного запиту
 */
class AuthInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        // Не додаємо заголовок для авторизації
        if (path.contains("auth/login") || path.contains("auth/register")) {
            return chain.proceed(request)
        }

        val token = runBlocking {
            dataStore.data.first()[PreferencesKeys.USER_TOKEN]
        }

        Log.d("Auth", "Path: $path, Token found: ${!token.isNullOrEmpty()}")

        val authenticatedRequest = if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(authenticatedRequest)
    }
}
