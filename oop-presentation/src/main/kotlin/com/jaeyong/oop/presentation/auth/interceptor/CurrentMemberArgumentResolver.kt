package com.jaeyong.oop.presentation.auth.interceptor

import com.jaeyong.oop.presentation.auth.CurrentMember
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * @CurrentMember 어노테이션이 붙은 컨트롤러 파라미터에
 * 현재 인증된 회원의 ID(Long)를 자동으로 주입하는 ArgumentResolver.
 *
 * JwtAuthenticationInterceptor가 JWT 토큰을 검증한 뒤
 * HttpServletRequest에 저장해 둔 memberId를 꺼내서 컨트롤러 파라미터로 전달한다.
 *
 * 사용 예시:
 * ```
 * @PostMapping("/logout")
 * fun logout(@CurrentMember memberId: Long): ApiResponse<Unit>
 * ```
 */
@Component
class CurrentMemberArgumentResolver : HandlerMethodArgumentResolver {

    /**
     * 이 resolver가 처리할 수 있는 파라미터인지 판별한다.
     * @CurrentMember 어노테이션이 있고 타입이 Long인 경우에만 true를 반환한다.
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentMember::class.java) &&
            parameter.parameterType == Long::class.java
    }

    /**
     * JwtAuthenticationInterceptor가 request attribute에 저장한 memberId를 꺼내 반환한다.
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Long {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!
        return request.getAttribute(JwtAuthenticationInterceptor.MEMBER_ID_ATTRIBUTE) as Long
    }
}
