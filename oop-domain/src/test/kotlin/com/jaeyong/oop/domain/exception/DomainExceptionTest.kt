package com.jaeyong.oop.domain.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * [도메인별 예외 관리 구조 테스트]
 * 멘토님의 의견을 반영하여 도메인별로 묶은 예외 처리가 
 * 올바른 메시지와 타입을 전달하는지 검증합니다.
 */
class DomainExceptionTest {

    @Test
    @DisplayName("Domain1Exception(회원) 생성 시 Enum에 정의된 메시지가 올바르게 담긴다")
    fun testDomain1ExceptionMessage() {
        // given
        val errorType = Domain1ErrorType.USER_NOT_FOUND
        
        // when
        val exception = Domain1Exception(errorType)
        
        // then
        assertThat(exception.message).isEqualTo("사용자를 찾을 수 없습니다.")
        assertThat(exception.type).isEqualTo(Domain1ErrorType.USER_NOT_FOUND)
    }

    @Test
    @DisplayName("Domain2Exception(잔액) 생성 시 Enum에 정의된 메시지가 올바르게 담긴다")
    fun testDomain2ExceptionMessage() {
        // given
        val errorType = Domain2ErrorType.INSUFFICIENT_FUNDS
        
        // when
        val exception = Domain2Exception(errorType)
        
        // then
        assertThat(exception.message).isEqualTo("잔액이 부족합니다.")
        assertThat(exception.type).isEqualTo(Domain2ErrorType.INSUFFICIENT_FUNDS)
    }

    @Test
    @DisplayName("Domain1Exception과 Domain2Exception은 서로 다른 도메인 예외임을 확인할 수 있다")
    fun testDomainExceptionSeparation() {
        // Domain1Exception과 Domain2Exception은 서로 상속 관계가 아니거나 
        // 독립된 도메인 영역임을 타입 체크로 확인할 수 있습니다.
        val userException = Domain1Exception(Domain1ErrorType.USER_NOT_FOUND)
        val balanceException = Domain2Exception(Domain2ErrorType.INSUFFICIENT_FUNDS)
        
        assertThat(userException).isNotInstanceOf(Domain2Exception::class.java)
        assertThat(balanceException).isNotInstanceOf(Domain1Exception::class.java)
    }
}
