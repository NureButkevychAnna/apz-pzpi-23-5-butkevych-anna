package com.example.radiation.repository.subscription

import com.example.radiation.data.models.Subscription

/**
 * Repository інтерфейс для управління підписками
 */
interface SubscriptionRepository {
    suspend fun createSubscription(channel: String, criteria: Map<String, Any>? = null): Result<Subscription>
    suspend fun getSubscriptions(): Result<List<Subscription>>
    suspend fun deleteSubscription(id: String): Result<Unit>
    suspend fun updateSubscription(id: String, criteria: Map<String, Any>? = null): Result<Subscription>
}

