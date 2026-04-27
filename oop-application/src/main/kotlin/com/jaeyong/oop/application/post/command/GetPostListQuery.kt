package com.jaeyong.oop.application.post.command

data class GetPostListQuery private constructor(
    val page: Int,
    val size: Int,
    val direction: String
) {
    companion object {
        fun of(page: Int, size: Int, direction: String) =
            GetPostListQuery(page, size, direction)
    }
}
