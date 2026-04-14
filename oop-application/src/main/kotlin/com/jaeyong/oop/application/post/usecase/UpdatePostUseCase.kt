package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.result.UpdatePostResult

interface UpdatePostUseCase {
    fun update(command: UpdatePostCommand): UpdatePostResult
}
