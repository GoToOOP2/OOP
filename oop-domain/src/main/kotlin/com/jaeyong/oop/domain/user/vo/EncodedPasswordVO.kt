package com.jaeyong.oop.domain.user.vo

data class EncodedPasswordVO private constructor(val value: String) {
    companion object {
        fun from(value: String): EncodedPasswordVO = EncodedPasswordVO(value)
    }
}
