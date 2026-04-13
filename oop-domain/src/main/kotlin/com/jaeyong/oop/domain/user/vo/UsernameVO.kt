package com.jaeyong.oop.domain.user.vo

// VO(Value Object): 값 자체가 식별자. 같은 value면 같은 객체로 취급
// private constructor: from()만 통해 생성 강제 → 생성 방식 일원화
data class UsernameVO private constructor(val value: String) {
    companion object {
        fun from(value: String): UsernameVO = UsernameVO(value)
    }
}
