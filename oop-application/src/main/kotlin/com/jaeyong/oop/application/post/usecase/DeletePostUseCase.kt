package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.command.DeletePostCommand

interface DeletePostUseCase {
    fun delete(command: DeletePostCommand)
}
