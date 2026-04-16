package com.jaeyong.oop.presentation.auth

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * [@CurrentUser][CurrentUser] 어노테이션이 붙은 컨트롤러 파라미터에 인증된 사용자명을 주입한다.
 *
 * [com.jaeyong.oop.presentation.filter.JwtAuthFilter]가 검증 후 request attribute("username")에
 * 저장한 값을 꺼내서 컨트롤러 파라미터에 주입한다.
 */
class CurrentUserArgumentResolver : HandlerMethodArgumentResolver {

    /**
     * [@CurrentUser][CurrentUser] 어노테이션이 붙은 파라미터인 경우에만 이 Resolver가 동작한다.
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java)

    /**
     * request attribute에서 username을 꺼내 반환한다.
     *
     * 비로그인 요청이면 attribute가 없으므로 UNAUTHORIZED 예외를 던진다.
     *
     * @return 인증된 사용자명
     * @throws BaseException UNAUTHORIZED — JWT 없거나 인증 실패
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): String {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
        return request?.getAttribute("username") as? String
            ?: throw BaseException(ErrorCode.UNAUTHORIZED)
    }
}
