package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import org.springframework.stereotype.Service

@Service
class TokenValidationService(
    private val jwtHandlerPort: JwtHandlerPort
) : TokenValidationUseCase {

    override fun validateAndExtract(token: String): String? {
        return jwtHandlerPort.validateAndExtract(token)
    }
}
