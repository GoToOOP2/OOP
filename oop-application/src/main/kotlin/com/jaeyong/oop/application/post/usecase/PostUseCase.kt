package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.common.DeletePostCommand
import com.jaeyong.oop.application.post.common.GetPostCommand
import com.jaeyong.oop.application.post.common.UpdatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult

interface PostUseCase {

    fun create(command: CreatePostCommand): CreatePostResult

    fun getById(command: GetPostCommand): GetPostResult

    fun getAll(): List<GetPostListResult>

    fun update(command: UpdatePostCommand): UpdatePostResult

    fun delete(command: DeletePostCommand)
}
