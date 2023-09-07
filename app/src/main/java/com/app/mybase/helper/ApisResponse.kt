package com.app.mybase.helper

sealed class ApisResponse<T> {
    data class Success<T>(var response: T) : ApisResponse<T>()
    data class Error<T>(var exception: String) : ApisResponse<T>()
    object Loading : ApisResponse<Nothing>()
    object Complete : ApisResponse<Nothing>()
}
