package com.jaeyong.oop.domain.health

import java.time.LocalDateTime

/**
 * 도메인 엔티티 (순수 Kotlin 클래스)
 */
data class Health(
    val id: Long? = null,
    val status: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
