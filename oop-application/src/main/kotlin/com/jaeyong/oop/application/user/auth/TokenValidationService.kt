package com.jaeyong.oop.application.user.auth

import com.jaeyong.oop.domain.user.port.JwtHandler
import org.springframework.stereotype.Service

@Service
class TokenValidationService(
    private val jwtProvider: JwtHandler
) : TokenValidationUseCase {

    override fun validateAndExtract(token: String): String? {
        return jwtProvider.validateAndExtract(token)
    }
}
