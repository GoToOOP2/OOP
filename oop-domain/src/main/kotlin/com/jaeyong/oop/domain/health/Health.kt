package com.jaeyong.oop.domain.health

import java.time.LocalDateTime

/**
 * 서버 상태 도메인 객체.
 *
 * 외부에서 직접 생성 금지 — 반드시 [ok] 또는 [restore] 팩토리 메서드를 통해 생성한다.
 */
data class Health private constructor(
    val id: Long? = null,
    val status: String,
    val createdAt: LocalDateTime
) {
    companion object {

        /**
         * 새 Health 상태를 생성한다 (status = "OK", createdAt = 현재 시각).
         *
         * @return 신규 [Health] 객체
         */
        fun ok(): Health = Health(status = "OK", createdAt = LocalDateTime.now())

        /**
         * DB에서 조회한 데이터로 Health를 복원한다.
         *
         * 비즈니스 규칙 검증 없이 순수 복원만 수행한다.
         *
         * @param id DB에서 할당된 식별자
         * @param status 저장된 상태 문자열
         * @param createdAt 저장된 생성 시각
         */
        fun restore(id: Long?, status: String, createdAt: LocalDateTime): Health = Health(id, status, createdAt)
    }
}
