package com.jaeyong.oop.application.user.auth

interface TokenValidationUseCase {
    fun validateAndExtract(token: String): String?
}
