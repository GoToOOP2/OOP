package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult

interface PostUseCase {
    fun create(command: CreatePostCommand): CreatePostResult
    fun get(command: GetPostCommand): GetPostResult
    fun getList(command: GetPostListCommand): GetPostListResult
    fun update(command: UpdatePostCommand): UpdatePostResult
    fun delete(command: DeletePostCommand)
}
