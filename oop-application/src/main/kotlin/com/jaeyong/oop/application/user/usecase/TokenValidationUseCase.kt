package com.jaeyong.oop.application.user.usecase

interface TokenValidationUseCase {
    fun validateAndExtract(token: String): String?
}
