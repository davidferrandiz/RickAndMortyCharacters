package com.davidferrandiz.rickandmortycharacters.domain.error

sealed interface AppError {
    data object NoConnection : AppError
    data object Timeout : AppError
    data class Http(val code: Int) : AppError
    data class Serialization(val detail: String?) : AppError
    data class Unknown(val cause: String?) : AppError
}
