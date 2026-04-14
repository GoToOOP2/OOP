package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult

interface CreatePostUseCase {
    fun create(command: CreatePostCommand): CreatePostResult
}
