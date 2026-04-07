package com.jaeyong.oop.presentation.user

import com.jaeyong.oop.application.user.usecase.JoinUseCase
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
    private val joinUseCase: JoinUseCase
) {
    @PostMapping("/join")
    fun join(@Valid @RequestBody request: JoinRequest): ResponseEntity<ApiResponse<Nothing?>> {
        joinUseCase.join(request.username, request.password)
        return ApiResponse.success(null, HttpStatus.CREATED)
    }
}
