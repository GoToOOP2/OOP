package com.jaeyong.oop.presentation.response

import com.jaeyong.oop.application.common.PageResult

/**
 * 페이지네이션 공통 응답 포맷.
 *
 * 모든 목록 조회 API는 이 구조로 응답한다.
 * totalPage는 totalCount와 size로 계산된 파생값.
 */
data class PageResponse<T>(
    val content: List<T>,
    val totalCount: Long,
    val totalPage: Int,
    val page: Int,
    val size: Int
) {
    companion object {
        fun <T, R> from(result: PageResult<T>, mapper: (T) -> R): PageResponse<R> = PageResponse(
            content = result.content.map(mapper),
            totalCount = result.totalCount,
            totalPage = if (result.size <= 0) 0 else ((result.totalCount + result.size - 1) / result.size).toInt(),
            page = result.page,
            size = result.size
        )
    }
}
