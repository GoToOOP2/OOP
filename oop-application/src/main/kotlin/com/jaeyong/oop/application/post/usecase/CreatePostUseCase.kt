package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult

interface CreatePostUseCase {

    /**
     * 새 게시글을 생성한다.
     *
     * @param command 제목, 내용, 작성자 ID를 담은 게시글 생성 요청
     * @return 생성된 게시글의 ID
     */
    fun create(command: CreatePostCommand): CreatePostResult
}
