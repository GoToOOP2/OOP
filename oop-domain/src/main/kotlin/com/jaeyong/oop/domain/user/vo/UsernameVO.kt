package com.jaeyong.oop.domain.user.vo

/**
 * 사용자명 Value Object.
 *
 * 값 자체가 식별자이며, 같은 value면 동일한 객체로 취급한다.
 * private constructor로 생성을 [from]으로 일원화한다.
 */
data class UsernameVO private constructor(val value: String) {
    companion object {
        /**
         * @param value 사용자명 문자열
         */
        fun from(value: String): UsernameVO = UsernameVO(value)
    }
}
