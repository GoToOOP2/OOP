package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.application.post.result.GetPostListResult

interface GetPostListUseCase {
    fun getList(command: GetPostListCommand): GetPostListResult
}
