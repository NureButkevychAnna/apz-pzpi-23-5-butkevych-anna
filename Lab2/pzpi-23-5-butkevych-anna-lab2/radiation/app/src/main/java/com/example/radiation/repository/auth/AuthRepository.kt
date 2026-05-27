package com.example.radiation.repository.auth

import com.example.radiation.data.models.RegisterResponse
import com.example.radiation.data.models.LoginResponse
import com.example.radiation.data.models.User

/**
 * Repository інтерфейс для аутентифікації
 */
interface AuthRepository {
    suspend fun register(email: String, password: String, name: String): Result<RegisterResponse>
    suspend fun login(email: String, password: String): Result<LoginResponse>
    suspend fun logout(): Result<Unit>
    suspend fun getUserById(id: String): Result<User>
    
    // Token management
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): String?
    suspend fun clearAuth()
}

