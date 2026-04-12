package com.jaeyong.oop.domain.user.vo

data class RawPasswordVO private constructor(val value: String) {
    companion object {
        fun from(value: String): RawPasswordVO = RawPasswordVO(value)
    }
}
