package com.jaeyong.oop.application.post.command

data class GetPostQuery private constructor(val id: Long) {
    companion object {
        fun of(id: Long) = GetPostQuery(id)
    }
}
