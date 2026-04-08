package com.jaeyong.oop.presentation.api

import com.jaeyong.oop.application.command.LoginCommand
import com.jaeyong.oop.application.command.SignupCommand
import com.jaeyong.oop.application.usecase.AuthUseCase
import com.jaeyong.oop.presentation.api.dto.LoginRequest
import com.jaeyong.oop.presentation.api.dto.ReissueRequest
import com.jaeyong.oop.presentation.api.dto.SignupRequest
import com.jaeyong.oop.presentation.api.dto.TokenResponse
import com.jaeyong.oop.presentation.auth.CurrentMember
import com.jaeyong.oop.presentation.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 인증 관련 REST API 컨트롤러.
 * 회원가입, 로그인, 토큰 재발급, 로그아웃 4개의 엔드포인트를 제공한다.
 *
 * - signup, login, reissue는 인터셉터 제외 경로 → 토큰 없이 접근 가능
 * - logout은 인터셉터 적용 → Authorization 헤더에 Access Token 필요
 *
 * 컨트롤러는 요청 검증과 DTO 변환만 담당하고,
 * 비즈니스 로직은 AuthUseCase(인바운드 포트)를 통해 서비스에 위임한다.
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authUseCase: AuthUseCase,
) {
    /**
     * 회원가입 API.
     * DTO(SignupRequest)를 검증한 뒤 Command로 변환하여 서비스에 전달한다.
     * 성공 시 201 Created 반환. 이메일 중복 시 DUPLICATE_EMAIL 예외.
     */
    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignupRequest, ): ResponseEntity<ApiResponse<Nothing>> {
        authUseCase.signup(SignupCommand(request.email, request.password, request.nickname))
        return ApiResponse.success(status = HttpStatus.CREATED)
    }

    /**
     * 로그인 API.
     * 이메일/비밀번호 검증 후 Access Token + Refresh Token을 발급한다.
     * 실패 시 LOGIN_FAILED 예외 (이메일 없음/비밀번호 틀림 구분하지 않음).
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<ApiResponse<TokenResponse>> {
        val result =
            authUseCase.login(
                LoginCommand(request.email, request.password),
            )
        return ApiResponse.success(TokenResponse(result.accessToken, result.refreshToken))
    }

    /**
     * 토큰 재발급 API.
     * Access Token이 만료되었을 때, Refresh Token으로 새 토큰 쌍을 발급받는다.
     * 토큰 로테이션 방식 — 재발급 시 Refresh Token도 새로 발급되고 이전 토큰은 무효화된다.
     */
    @PostMapping("/reissue")
    fun reissue(
        @Valid @RequestBody request: ReissueRequest,
    ): ResponseEntity<ApiResponse<TokenResponse>> {
        val result = authUseCase.reissue(request.refreshToken)
        return ApiResponse.success(TokenResponse(result.accessToken, result.refreshToken))
    }

    /**
     * 로그아웃 API.
     * @CurrentMember: 인터셉터가 토큰에서 추출한 memberId가 자동 주입된다.
     * DB에서 Refresh Token을 삭제하여 이후 재발급을 차단한다.
     */
    @PostMapping("/logout")
    fun logout(
        @CurrentMember memberId: Long,
    ): ResponseEntity<ApiResponse<Nothing>> {
        authUseCase.logout(memberId)
        return ApiResponse.success()
    }
}
