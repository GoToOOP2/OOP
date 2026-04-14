package com.jaeyong.oop.presentation.auth

/**
 * 컨트롤러 파라미터에 현재 로그인한 사용자명을 주입받기 위한 어노테이션.
 *
 * [com.jaeyong.oop.presentation.auth.CurrentUserArgumentResolver]가 이 어노테이션을 감지해
 * request attribute에서 username을 꺼내 파라미터에 주입한다.
 *
 * @Target VALUE_PARAMETER — 함수 파라미터에만 적용 가능
 * @Retention RUNTIME — Spring이 런타임에 이 어노테이션을 감지해야 하므로 유지
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentUser
