package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import com.jaeyong.oop.domain.user.port.JwtProvider
import org.springframework.stereotype.Service

@Service
class TokenValidationService(
    private val jwtProvider: JwtProvider
) : TokenValidationUseCase {

    override fun validateAndExtract(token: String): String? {
        return if (jwtProvider.isValid(token)) jwtProvider.extractUsername(token) else null
    }
}
