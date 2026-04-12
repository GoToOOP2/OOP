package com.jaeyong.oop.domain.user.vo

data class UsernameVO private constructor(val value: String) {
    companion object {
        fun from(value: String): UsernameVO = UsernameVO(value)
    }
}
