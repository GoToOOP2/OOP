package com.jaeyong.oop.presentation.auth

/**
 * [역할] 컨트롤러 파라미터에서 현재 로그인한 사용자 정보를 주입받기 위한 전용 이름표(어노테이션)
 */
@Target(AnnotationTarget.VALUE_PARAMETER) // 함수 파라미터에만 붙일 수 있도록 제한
@Retention(AnnotationRetention.RUNTIME)   // 실행 중(Runtime)에 Spring이 이 이름표를 보고 리졸버를 동작시켜야 하므로 유지함
annotation class CurrentUser
