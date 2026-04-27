package com.jaeyong.oop.application.common

/**
 * 페이지네이션 조회 결과 공통 타입.
 *
 * 모든 목록 조회 UseCase는 이 타입으로 결과를 반환한다.
 * content는 도메인별 Result, 나머지는 페이지 메타데이터.
 */
data class PageResult<T>(
    val content: List<T>,
    val totalCount: Long,
    val page: Int,
    val size: Int
) {
    companion object {
        fun <T> of(content: List<T>, totalCount: Long, page: Int, size: Int): PageResult<T> =
            PageResult(content, totalCount, page, size)
    }
}
