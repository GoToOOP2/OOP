package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.application.post.command.GetPostQuery
import com.jaeyong.oop.application.post.command.GetPostListQuery
import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.common.PageResult
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult

interface PostUseCase {
    fun create(command: CreatePostCommand): CreatePostResult
    fun get(command: GetPostQuery): GetPostResult
    fun getList(command: GetPostListQuery): PageResult<GetPostResult>
    fun update(command: UpdatePostCommand): UpdatePostResult
    fun delete(command: DeletePostCommand)
}
