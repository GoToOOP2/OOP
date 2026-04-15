package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult

interface GetPostUseCase {
    fun getById(id: Long): GetPostResult
    fun getAll(): List<GetPostListResult>
}
