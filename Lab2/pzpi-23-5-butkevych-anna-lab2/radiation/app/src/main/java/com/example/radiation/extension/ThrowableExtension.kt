package com.example.radiation.extension

import retrofit2.HttpException

/**
 * Extension functions for human-readable HTTP/API error output.
 */

fun Throwable.toDetailedMessage(defaultMessage: String): String {
    return when (this) {
        is HttpException -> buildHttpExceptionMessage(defaultMessage)
        else -> message?.takeIf { it.isNotBlank() } ?: defaultMessage
    }
}

fun Throwable.toDetailedMessage(): String {
    return toDetailedMessage("Unexpected error")
}

private fun HttpException.buildHttpExceptionMessage(defaultMessage: String): String {
    val responseBody = try {
        response()?.errorBody()?.string()?.trim().orEmpty()
    } catch (_: Exception) {
        ""
    }

    return buildString {
        append("HTTP ")
        append(code())
        val messageText = message?.takeIf { it.isNotBlank() }
        if (messageText != null) {
            append(" — ")
            append(messageText)
        } else if (defaultMessage.isNotBlank()) {
            append(" — ")
            append(defaultMessage)
        }

        if (responseBody.isNotBlank()) {
            append("\n\nResponse body:\n")
            append(responseBody)
        }
    }
}

