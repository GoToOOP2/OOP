package com.jaeyong.oop.application.command

/**
 * 회원가입 유스케이스 입력 객체.
 * presentation의 SignupRequest(DTO)와 분리되어 있어서,
 * HTTP 요청 형식이 바뀌어도 application 레이어는 영향받지 않는다.
 * 컨트롤러가 DTO → Command 변환을 책임진다.
 */
data class SignupCommand(
    val email: String,
    val password: String,
    val nickname: String,
)
