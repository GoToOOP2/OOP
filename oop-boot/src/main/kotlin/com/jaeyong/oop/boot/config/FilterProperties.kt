package com.jaeyong.oop.boot.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * JWT 필터 URL 패턴 설정 (`application.yml` → `filter.*`).
 *
 * @property urlPatterns JWT 인증 필터를 적용할 URL 패턴 목록
 */
@ConfigurationProperties(prefix = "filter")
data class FilterProperties(
    val urlPatterns: List<String>
)
