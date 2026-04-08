package com.jaeyong.oop.application.command

/**
 * 로그인 유스케이스 입력 객체.
 * presentation의 LoginRequest(DTO)와 분리되어 있다.
 */
data class LoginCommand(
    val email: String,
    val password: String,
)
