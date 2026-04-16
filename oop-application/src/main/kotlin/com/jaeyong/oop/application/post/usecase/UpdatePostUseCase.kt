package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.common.UpdatePostCommand
import com.jaeyong.oop.application.post.result.UpdatePostResult

interface UpdatePostUseCase {

    /**
     * 게시글을 수정한다.
     *
     * @param command 게시글 ID, 제목, 내용, 요청자 ID를 담은 수정 요청
     * @return 수정된 게시글 상세 정보
     * @throws com.jaeyong.oop.common.exception.BaseException 게시글이 존재하지 않거나 권한이 없을 때
     */
    fun update(command: UpdatePostCommand): UpdatePostResult
}
