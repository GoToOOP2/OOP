package com.jaeyong.oop.presentation.post.response

import com.jaeyong.oop.application.post.result.GetPostListResult

data class PostListResponse(
    val posts: List<PostResponse>,
    val totalCount: Long,
    val page: Int,
    val size: Int
) {
    companion object {
        fun of(result: GetPostListResult) = PostListResponse(
            posts = result.posts.map { PostResponse.of(it) },
            totalCount = result.totalCount,
            page = result.page,
            size = result.size
        )
    }
}
