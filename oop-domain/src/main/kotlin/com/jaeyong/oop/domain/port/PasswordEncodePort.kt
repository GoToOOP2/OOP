package com.jaeyong.oop.domain.port

/**
 * 비밀번호 해싱/검증 아웃바운드 포트.
 * 도메인이 정의하고, infrastructure의 BcryptAdapter가 구현한다.
 * 비밀번호 저장 시 encode(), 로그인 시 matches()를 사용한다.
 */
interface PasswordEncodePort {
    /** 평문 비밀번호를 해싱한다. 회원가입 시 DB에 저장하기 전 호출.
     * 비밀번호 해싱은 핵심 비즈니스 로직이 아님 -> 기술 (비번 해싱 기술이 언제든지 바뀔 있음)
     * 핵심 비즈니스 로직: 비밀번호를 안전하게 저장해야 한다*/
    fun encode(rawPassword: String): String

    /** 평문 비밀번호가 해싱된 비밀번호와 일치하는지 검증한다. 로그인 시 호출.
     * 해싱된 비밀번호 인증은 핵심 비즈니스 로직이 아님 -> 기술
     * 핵심 비즈니스 로직: 로그인시 비밀번호가 일치해야 한다.*/
    fun matches(rawPassword: String, encodedPassword: String, ): Boolean
}