package com.jaeyong.oop.presentation.user.api

import com.jaeyong.oop.application.user.common.JoinCommand
import com.jaeyong.oop.application.user.common.LoginCommand
import com.jaeyong.oop.application.user.common.RefreshCommand
import com.jaeyong.oop.application.user.usecase.JoinUseCase
import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.application.user.usecase.RefreshUseCase
import com.jaeyong.oop.presentation.auth.CurrentUser
import com.jaeyong.oop.presentation.response.ApiResponse
import com.jaeyong.oop.presentation.user.request.JoinRequest
import com.jaeyong.oop.presentation.user.request.LoginRequest
import com.jaeyong.oop.presentation.user.request.RefreshRequest
import com.jaeyong.oop.presentation.user.response.TokenResponse
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
@RequestMapping("/users")
class UserController(
    private val joinUseCase: JoinUseCase,
    private val loginUseCase: LoginUseCase,
    private val refreshUseCase: RefreshUseCase
) : UserControllerDocs {

    private val log = LoggerFactory.getLogger(javaClass)

    // TODO: 테스트용 - 확인 후 삭제
    @GetMapping("/me")
    override fun me(@CurrentUser username: String?): ResponseEntity<ApiResponse<String>> {
        log.info("CurrentUser username = {}", username)
        return ApiResponse.success(username ?: "비로그인", HttpStatus.OK)
    }

    @PostMapping("/join")
    override fun join(@Valid @RequestBody request: JoinRequest): ResponseEntity<ApiResponse<Nothing>> {
        joinUseCase.join(JoinCommand.of(username = request.username, password = request.password))
        return ApiResponse.success(HttpStatus.CREATED)
    }

    @PostMapping("/login")
    override fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val result = loginUseCase.login(LoginCommand.of(username = request.username, password = request.password))
        return ApiResponse.success(TokenResponse.of(accessToken = result.token, refreshToken = result.refreshToken), HttpStatus.OK)
    }

    @PostMapping("/refresh")
    override fun refresh(@RequestBody request: RefreshRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val result = refreshUseCase.refresh(RefreshCommand.of(request.refreshToken))
        return ApiResponse.success(TokenResponse.of(accessToken = result.accessToken, refreshToken = result.refreshToken), HttpStatus.OK)
    }
}
