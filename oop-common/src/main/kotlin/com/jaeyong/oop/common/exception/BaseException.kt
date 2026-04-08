package com.jaeyong.oop.common.exception

/**
 * 모든 비즈니스 예외의 부모 클래스
 *
 * 각 레이어(domain, application, infrastructure)의 예외는
 * 이 클래스를 상속하고, ErrorCode를 지정한다.
 */
open class BaseException(
    val errorCode: ErrorCode,
    cause: Throwable? = null,
) : RuntimeException(errorCode.code, cause)
