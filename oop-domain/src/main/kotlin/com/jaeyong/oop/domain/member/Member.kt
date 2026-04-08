package com.jaeyong.oop.domain.member

/**
 * 회원 도메인 모델.
 * 순수 Kotlin 클래스로, JPA/Spring 등 프레임워크에 의존하지 않는다.
 * DB 저장 전에는 id가 null이고, 저장 후 자동 생성된 값이 채워진다.
 * password는 항상 해싱된 상태로 저장된다 (평문이 들어오는 일 없음).
 */
data class Member(
    val id: Long? = null,
    val email: String,
    val password: String,
    val nickname: String,
)
