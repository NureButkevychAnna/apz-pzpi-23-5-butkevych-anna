package com.example.radiation.config

/**
 * Конфігурація сервера для доступу до Radiation Monitoring API
 */
object ServerConfig {
    // Base URL - може змінюватися залежно від оточення
    // DEV: http://localhost:3000/api/
    // PROD: буде замінено на production URL
    const val BASE_URL = "http://147.15.143.53:3001/api/"

    // Таймаути для запитів
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    
    // API endpoints
    object Endpoints {
        const val LOGIN = "auth/login"
        const val REGISTER = "auth/register"
        const val GET_DATA = "data"
        const val POST_DATA = "data"
        // Додавайте ваші endpoints тут
    }
}


