package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.TokenValidationCommand
import com.jaeyong.oop.application.user.result.TokenValidationResult
import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import org.springframework.stereotype.Service

@Service
class TokenValidationService(
    private val jwtHandlerPort: JwtHandlerPort
) : TokenValidationUseCase {

    override fun validateAndExtract(command: TokenValidationCommand): TokenValidationResult {
        val username = jwtHandlerPort.validateAndExtract(TokenVO.from(command.token))
        return TokenValidationResult.of(username?.value)
    }
}
