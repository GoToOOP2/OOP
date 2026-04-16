package com.jaeyong.oop.application.post.command

data class GetPostListCommand private constructor(val page: Int, val size: Int) {
    companion object {
        fun of(page: Int, size: Int) = GetPostListCommand(page, size)
    }
}
