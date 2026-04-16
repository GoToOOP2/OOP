package com.jaeyong.oop.application.post.result

import com.jaeyong.oop.domain.post.Post

data class GetPostListResult private constructor(
    val posts: List<GetPostResult>,
    val totalCount: Long,
    val page: Int,
    val size: Int
) {
    companion object {
        fun of(posts: List<Post>, totalCount: Long, page: Int, size: Int) =
            GetPostListResult(posts.map { GetPostResult.of(it) }, totalCount, page, size)
    }
}
