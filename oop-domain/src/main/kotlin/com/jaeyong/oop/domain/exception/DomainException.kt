package com.jaeyong.oop.domain.exception

/**
 * [도메인 1: 회원(User) 관련]
 */
enum class Domain1ErrorType(val message: String) {
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    ALREADY_EXISTS("이미 존재하는 사용자입니다.")
}

class Domain1Exception(val type: Domain1ErrorType) : RuntimeException(type.message)


/**
 * [도메인 2: 잔액(Balance) 관련]
 */
enum class Domain2ErrorType(val message: String) {
    INSUFFICIENT_FUNDS("잔액이 부족합니다."),
    LIMIT_EXCEEDED("이체 한도를 초과했습니다.")
}

class Domain2Exception(val type: Domain2ErrorType) : RuntimeException(type.message)
