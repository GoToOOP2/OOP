package com.oop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * 애플리케이션 진입점 (Composition Root)
 *
 * @SpringBootApplication의 컴포넌트 스캔은 com.oop 패키지 하위 전체를 탐색하므로,
 * 모든 모듈(domain, application, infrastructure, presentation)의 빈이 자동으로 등록됩니다.
 */
@SpringBootApplication
class OopApplication

fun main(args: Array<String>) {
    runApplication<OopApplication>(*args)
}

