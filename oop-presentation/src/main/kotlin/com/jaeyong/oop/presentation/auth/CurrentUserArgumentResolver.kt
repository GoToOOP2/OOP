package com.jaeyong.oop.presentation.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * [역할] Spring MVC가 컨트롤러를 실행할 때, @CurrentUser가 붙은 파라미터에 실제 값을 채워주는 '배달부'
 */
class CurrentUserArgumentResolver : HandlerMethodArgumentResolver {

    /**
     * 1단계: 조건 확인 (누구에게 배달할 것인가?)
     * 컨트롤러의 파라미터에 @CurrentUser 어노테이션이 붙어있는지 확인합니다.
     * 결과가 true인 경우에만 아래의 resolveArgument()가 실행됩니다.
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java)

    /**
     * 2단계: 값 결정 및 전달 (무엇을 배달할 것인가?)
     * Filter(JwtAuthFilter)가 미리 저장해둔 정보를 꺼내서 컨트롤러 파라미터에 꽂아줍니다.
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): String? {
        // Spring의 추상화된 요청 객체에서 실제 'HttpServletRequest'를 꺼냅니다.
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
        
        // [중요] JwtAuthFilter에서 request.setAttribute("username", ...)로 넣어둔 값을 꺼냅니다.
        // 이 리턴값이 컨트롤러의 파라미터(String) 자리에 자동으로 주입됩니다.
        // 유저네임으로 꺼내서 파라미터에 주입
        return request?.getAttribute("username") as? String
    }
}
