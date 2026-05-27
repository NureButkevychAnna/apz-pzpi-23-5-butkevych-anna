package com.example.radiation.repository.subscription

import com.example.radiation.network.ApiService
import com.example.radiation.data.models.*
import javax.inject.Inject

/**
 * Реалізація SubscriptionRepository
 */
class SubscriptionRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SubscriptionRepository {
    
    override suspend fun createSubscription(channel: String, criteria: Map<String, Any>?): Result<Subscription> {
        return try {
            val subscription = apiService.createSubscription(CreateSubscriptionRequest(channel, criteria))
            Result.success(subscription)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSubscriptions(): Result<List<Subscription>> {
        return try {
            val response = apiService.getSubscriptions()
            Result.success(response.subscriptions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteSubscription(id: String): Result<Unit> {
        return try {
            apiService.deleteSubscription(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSubscription(id: String, criteria: Map<String, Any>?): Result<Subscription> {
        return try {
            val subscription = apiService.updateSubscription(id, UpdateSubscriptionRequest(criteria))
            Result.success(subscription)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

