package com.jaeyong.oop.presentation.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * [@CurrentUser][CurrentUser] 어노테이션이 붙은 컨트롤러 파라미터에 인증된 사용자 ID를 주입한다.
 *
 * [com.jaeyong.oop.presentation.filter.JwtAuthFilter]가 검증 후 request attribute("userId")에
 * 저장한 값을 꺼내서 컨트롤러 파라미터에 주입한다.
 */
class CurrentUserArgumentResolver : HandlerMethodArgumentResolver {

    /**
     * [@CurrentUser][CurrentUser] 어노테이션이 붙은 파라미터인 경우에만 이 Resolver가 동작한다.
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java)

    /**
     * request attribute에서 userId를 꺼내 반환한다.
     *
     * 비로그인 요청이면 attribute가 없으므로 null을 반환한다.
     *
     * @return 인증된 사용자 ID, 비로그인이면 null
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Long? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
        return request?.getAttribute("userId") as? Long
    }
}
