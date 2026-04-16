package com.jaeyong.oop.application.post.command

data class GetPostCommand private constructor(val id: Long) {
    companion object {
        fun of(id: Long) = GetPostCommand(id)
    }
}
