package com.jaeyong.oop.application.user.usecase

interface LoginUseCase {
    fun login(username: String, password: String): String
}
