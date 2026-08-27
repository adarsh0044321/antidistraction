package com.antigravity.antidistraction.util

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val exception: Throwable, val message: String? = exception.localizedMessage) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}
