package com.jaeyong.oop.domain.exception

/**
 * 도메인 계층의 최상위 예외 클래스입니다.
 * 외부 규격(ErrorCode)을 모르는 순수 비즈니스 상황만을 나타냅니다.
 */
open class DomainException(message: String) : RuntimeException(message)

/**
 * 사용자를 찾을 수 없는 비즈니스 예외 상황을 정의합니다.
 */
class UserNotFoundException : DomainException("사용자를 찾을 수 없습니다.")

/**
 * 잔액이 부족한 비즈니스 예외 상황을 정의합니다.
 */
class InvalidBalanceException : DomainException("잔액이 부족합니다.")
