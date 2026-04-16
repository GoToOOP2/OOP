package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.common.DeletePostCommand

interface DeletePostUseCase {

    /**
     * 게시글을 삭제한다.
     *
     * @param command 게시글 ID, 요청자 ID를 담은 삭제 요청
     * @throws com.jaeyong.oop.common.exception.BaseException 게시글이 존재하지 않거나 권한이 없을 때
     */
    fun delete(command: DeletePostCommand)
}
