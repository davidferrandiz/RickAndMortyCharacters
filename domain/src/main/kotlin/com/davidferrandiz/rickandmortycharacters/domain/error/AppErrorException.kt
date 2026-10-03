package com.davidferrandiz.rickandmortycharacters.domain.error

class AppErrorException(val error: AppError) : Exception(error.toString())

fun Throwable.asAppError(): AppError = (this as? AppErrorException)?.error ?: AppError.Unknown(message)
