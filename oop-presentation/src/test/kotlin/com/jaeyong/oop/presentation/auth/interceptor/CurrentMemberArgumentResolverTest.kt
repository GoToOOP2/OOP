package com.jaeyong.oop.presentation.auth.interceptor

import com.jaeyong.oop.presentation.auth.CurrentMember
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest

class CurrentMemberArgumentResolverTest {
    private val resolver = CurrentMemberArgumentResolver()

    @Test
    @DisplayName("@CurrentMember Long 파라미터를 지원해야 한다")
    fun `CurrentMember Long 파라미터를 지원한다`() {
        // given
        val parameter = getMethodParameter("withCurrentMember")

        // when & then
        assertThat(resolver.supportsParameter(parameter)).isTrue()
    }

    @Test
    @DisplayName("@CurrentMember가 없는 파라미터는 지원하지 않아야 한다")
    fun `CurrentMember가 없는 파라미터는 지원하지 않는다`() {
        // given
        val parameter = getMethodParameter("withoutCurrentMember")

        // when & then
        assertThat(resolver.supportsParameter(parameter)).isFalse()
    }

    @Test
    @DisplayName("request attribute에서 memberId를 꺼내 반환해야 한다")
    fun `request attribute에서 memberId를 꺼내 반환한다`() {
        // given
        val request = MockHttpServletRequest()
        request.setAttribute(JwtAuthenticationInterceptor.MEMBER_ID_ATTRIBUTE, 1L)
        val webRequest = ServletWebRequest(request)
        val parameter = getMethodParameter("withCurrentMember")

        // when
        val memberId = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertThat(memberId).isEqualTo(1L)
    }

    // 테스트용 메서드 — 파라미터의 어노테이션 유무로 supportsParameter를 검증한다
    @Suppress("unused")
    fun withCurrentMember(@CurrentMember memberId: Long) {}

    @Suppress("unused")
    fun withoutCurrentMember(memberId: Long) {}

    private fun getMethodParameter(methodName: String): MethodParameter {
        val method = this::class.java.getMethod(methodName, Long::class.java)
        return MethodParameter(method, 0)
    }
}
