package com.jaeyong.oop.application.post.command

data class GetPostListQuery private constructor(val page: Int, val size: Int) {
    companion object {
        fun of(page: Int, size: Int) = GetPostListQuery(page, size)
    }
}
