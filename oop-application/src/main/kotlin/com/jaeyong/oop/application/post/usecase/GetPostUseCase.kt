package com.jaeyong.oop.application.post.usecase

import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult

interface GetPostUseCase {

    /**
     * ID로 게시글을 단건 조회한다.
     *
     * @param id 조회할 게시글 ID
     * @return 게시글 상세 정보
     * @throws com.jaeyong.oop.common.exception.BaseException 게시글이 존재하지 않을 때 (NOT_FOUND)
     */
    fun getById(id: Long): GetPostResult

    /**
     * 전체 게시글 목록을 조회한다.
     *
     * @return 게시글 목록
     */
    fun getAll(): List<GetPostListResult>
}
