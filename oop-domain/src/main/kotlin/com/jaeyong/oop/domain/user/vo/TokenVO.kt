package com.jaeyong.oop.domain.user.vo

data class TokenVO private constructor(val value: String) {
    companion object {
        fun from(value: String): TokenVO = TokenVO(value)
    }
}
