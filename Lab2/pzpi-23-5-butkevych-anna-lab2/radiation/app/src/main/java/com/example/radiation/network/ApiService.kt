package com.example.radiation.network

import retrofit2.http.*
import com.example.radiation.data.models.*

/**
 * API сервіс для Radiation Monitoring System
 * Взято з Swagger документації
 * 
 * Адмін endpoints видалені - адмінка буде на вебі
 */
interface ApiService {

    // ============ AUTH ============
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("auth/logout")
    suspend fun logout()
    
    @GET("auth/users/{id}")
    suspend fun getUserById(@Path("id") id: String): User
    
    // ============ DEVICES ============
    
    @GET("devices")
    suspend fun getDevices(): DeviceListResponse
    
    @POST("devices")
    suspend fun createDevice(@Body request: CreateDeviceRequest): CreateDeviceResponse
    
    @GET("devices/{id}")
    suspend fun getDeviceById(@Path("id") id: String): Device
    
    @PUT("devices/{id}")
    suspend fun updateDevice(
        @Path("id") id: String,
        @Body request: UpdateDeviceRequest
    ): Device
    
    @DELETE("devices/{id}")
    suspend fun deleteDevice(@Path("id") id: String): SuccessResponse
    
    // ============ SENSOR READINGS ============
    
    @POST("readings")
    suspend fun submitReading(@Body request: CreateReadingRequest): SuccessResponse
    
    @GET("readings")
    suspend fun getReadings(
        @Query("device_id") deviceId: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("since") since: String? = null
    ): ReadingsListResponse
    
    // ============ ALERTS ============
    
    @GET("alerts")
    suspend fun getAlerts(
        @Query("level") level: String? = null,
        @Query("acknowledged") acknowledged: Boolean? = null
    ): AlertsListResponse
    
    @GET("alerts/{id}")
    suspend fun getAlertById(@Path("id") id: String): Alert
    
    @POST("alerts/{id}/ack")
    suspend fun acknowledgeAlert(@Path("id") id: String): SuccessResponse
    
    // ============ SUBSCRIPTIONS ============
    
    @POST("subscriptions")
    suspend fun createSubscription(@Body request: CreateSubscriptionRequest): Subscription
    
    @GET("subscriptions")
    suspend fun getSubscriptions(): SubscriptionsListResponse
    
    @DELETE("subscriptions/{id}")
    suspend fun deleteSubscription(@Path("id") id: String): SuccessResponse
    
    @PUT("subscriptions/{id}")
    suspend fun updateSubscription(
        @Path("id") id: String,
        @Body request: UpdateSubscriptionRequest
    ): Subscription
}





