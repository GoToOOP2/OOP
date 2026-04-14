package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.application.post.result.GetPostResult

interface GetPostUseCase {
    fun get(command: GetPostCommand): GetPostResult
}
