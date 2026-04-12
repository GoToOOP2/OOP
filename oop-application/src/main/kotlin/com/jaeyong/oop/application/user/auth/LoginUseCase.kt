package com.jaeyong.oop.application.user.auth

interface LoginUseCase {
    fun login(username: String, password: String): String
}
