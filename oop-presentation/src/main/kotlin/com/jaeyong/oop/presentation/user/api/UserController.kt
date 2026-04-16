package com.jaeyong.oop.presentation.user.api

import com.jaeyong.oop.application.user.common.JoinCommand
import com.jaeyong.oop.application.user.common.LoginCommand
import com.jaeyong.oop.application.user.common.RefreshCommand
import com.jaeyong.oop.application.user.usecase.JoinUseCase
import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.application.user.usecase.RefreshUseCase
import com.jaeyong.oop.presentation.response.ApiResponse
import com.jaeyong.oop.presentation.user.request.JoinRequest
import com.jaeyong.oop.presentation.user.request.LoginRequest
import com.jaeyong.oop.presentation.user.request.RefreshRequest
import com.jaeyong.oop.presentation.user.response.TokenResponse
import com.jaeyong.oop.presentation.auth.CurrentUser
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val joinUseCase: JoinUseCase,
    private val loginUseCase: LoginUseCase,
    private val refreshUseCase: RefreshUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // TODO: 테스트용 - 확인 후 삭제
    @GetMapping("/me")
    fun me(@CurrentUser userId: Long?): ResponseEntity<ApiResponse<String>> {
        log.info("CurrentUser userId = {}", userId)
        return ApiResponse.success(userId?.toString() ?: "비로그인", HttpStatus.OK)
    }

    /**
     * 회원가입 — username, password를 받아 사용자를 등록한다.
     *
     * @param request username, password
     * @return 201 Created
     */
    @PostMapping("/join")
    fun join(@Valid @RequestBody request: JoinRequest): ResponseEntity<ApiResponse<Nothing>> {
        joinUseCase.join(JoinCommand.of(username = request.username, password = request.password))
        return ApiResponse.success(HttpStatus.CREATED)
    }

    /**
     * 로그인 — 인증 성공 시 access token과 refresh token을 반환한다.
     *
     * @param request username, password
     * @return 200 OK, body: accessToken, refreshToken
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val result = loginUseCase.login(LoginCommand.of(username = request.username, password = request.password))
        return ApiResponse.success(TokenResponse.of(accessToken = result.token, refreshToken = result.refreshToken), HttpStatus.OK)
    }

    /**
     * 토큰 갱신 — refresh token을 검증하고 access/refresh token을 재발급한다 (Rotate).
     *
     * @param request refresh token
     * @return 200 OK, body: 새 accessToken, 새 refreshToken
     */
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val result = refreshUseCase.refresh(RefreshCommand.of(request.refreshToken))
        return ApiResponse.success(TokenResponse.of(accessToken = result.accessToken, refreshToken = result.refreshToken), HttpStatus.OK)
    }
}
