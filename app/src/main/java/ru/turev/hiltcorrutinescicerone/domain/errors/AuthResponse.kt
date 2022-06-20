package ru.turev.hiltcorrutinescicerone.domain.errors

sealed class AuthResponse {
    object Success : AuthResponse()
    class Error(val error: String) : AuthResponse()
}
