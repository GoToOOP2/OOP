package com.jaeyong.oop.domain.health

import java.time.LocalDateTime

/**
 * 도메인 엔티티 (순수 Kotlin 클래스)
 */
data class Health private constructor(
    val id: Long? = null,
    val status: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun ok(): Health = Health(status = "OK", createdAt = LocalDateTime.now())
        fun restore(id: Long?, status: String, createdAt: LocalDateTime): Health = Health(id, status, createdAt)
    }
}
