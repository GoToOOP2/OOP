package com.jaeyong.oop.presentation.user

import com.jaeyong.oop.application.user.usecase.JoinUseCase
import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.presentation.response.ApiResponse
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
    fun join(@Valid @RequestBody request: JoinRequest): ResponseEntity<ApiResponse<Nothing?>> {
        joinUseCase.join(request.username, request.password)
        return ApiResponse.success(null, HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<Map<String, String>>> {
        val token = loginUseCase.login(request.username, request.password)
        return ApiResponse.success(mapOf("accessToken" to token), HttpStatus.OK)
    }
}
