package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.annotation.StringRes
import com.davidferrandiz.rickandmortycharacters.core.ui.R
import com.davidferrandiz.rickandmortycharacters.domain.error.AppError

private const val HTTP_TOO_MANY_REQUESTS = 429

@get:StringRes
val AppError.labelRes: Int
    get() = when (this) {
        AppError.NoConnection -> R.string.error_label_no_connection
        AppError.Timeout -> R.string.error_label_timeout
        is AppError.Http -> when (code) {
            HTTP_TOO_MANY_REQUESTS -> R.string.error_label_too_many_requests
            else -> R.string.error_label_server
        }
        is AppError.Serialization, is AppError.Unknown -> R.string.error_label_generic
    }

@get:StringRes
val AppError.messageRes: Int
    get() = when (this) {
        AppError.NoConnection -> R.string.error_message_no_connection
        AppError.Timeout -> R.string.error_message_timeout
        is AppError.Http -> when (code) {
            HTTP_TOO_MANY_REQUESTS -> R.string.error_message_too_many_requests
            else -> R.string.error_message_server
        }
        is AppError.Serialization, is AppError.Unknown -> R.string.error_message_generic
    }
