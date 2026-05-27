package com.example.radiation.repository.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.radiation.network.ApiService
import com.example.radiation.data.models.*
import com.example.radiation.config.PreferencesKeys
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * Реалізація AuthRepository
 */
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStore<Preferences>
) : AuthRepository {
    
    override suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_TOKEN] = token
        }
    }
    
    override suspend fun getToken(): String? {
        return dataStore.data.firstOrNull()?.get(PreferencesKeys.USER_TOKEN)
    }
    
    override suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_ID] = userId
        }
    }
    
    override suspend fun getUserId(): String? {
        return dataStore.data.firstOrNull()?.get(PreferencesKeys.USER_ID)
    }
    
    override suspend fun clearAuth() {
        dataStore.edit { prefs ->
            prefs.remove(PreferencesKeys.USER_TOKEN)
            prefs.remove(PreferencesKeys.USER_ID)
            prefs.remove(PreferencesKeys.USER_EMAIL)
            prefs[PreferencesKeys.IS_LOGGED_IN] = false
        }
    }
    
    override suspend fun register(email: String, password: String, name: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(RegisterRequest(email, password, name))
            saveToken(response.token)
            saveUserId(response.user.id)
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.USER_EMAIL] = response.user.email
                prefs[PreferencesKeys.IS_LOGGED_IN] = true
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            saveToken(response.token)
            saveUserId(response.user.id)
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.USER_EMAIL] = response.user.email
                prefs[PreferencesKeys.IS_LOGGED_IN] = true
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            apiService.logout()
            clearAuth()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserById(id: String): Result<User> {
        return try {
            val user = apiService.getUserById(id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

