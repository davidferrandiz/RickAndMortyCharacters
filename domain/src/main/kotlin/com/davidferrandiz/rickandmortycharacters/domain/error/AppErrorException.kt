package com.davidferrandiz.rickandmortycharacters.domain.error

class AppErrorException(val error: AppError) : Exception(error.toString())
