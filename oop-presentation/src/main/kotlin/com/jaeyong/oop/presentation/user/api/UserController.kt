package com.jaeyong.oop.presentation.user.api

import com.jaeyong.oop.application.user.common.JoinCommand
import com.jaeyong.oop.application.user.common.LoginCommand
import com.jaeyong.oop.application.user.usecase.JoinUseCase
import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.presentation.response.ApiResponse
import com.jaeyong.oop.presentation.user.request.JoinRequest
import com.jaeyong.oop.presentation.user.request.LoginRequest
import com.jaeyong.oop.presentation.user.response.TokenResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val joinUseCase: JoinUseCase,
    private val loginUseCase: LoginUseCase
) {
    @PostMapping("/join")
    fun join(@Valid @RequestBody request: JoinRequest): ResponseEntity<ApiResponse<Nothing>> {
        joinUseCase.join(JoinCommand(username = request.username, password = request.password))
        return ApiResponse.success(HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val result = loginUseCase.login(LoginCommand(username = request.username, password = request.password))
        return ApiResponse.success(TokenResponse(accessToken = result.token), HttpStatus.OK)
    }
}
