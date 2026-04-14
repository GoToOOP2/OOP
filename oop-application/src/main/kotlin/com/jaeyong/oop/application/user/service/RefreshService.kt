package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.RefreshCommand
import com.jaeyong.oop.application.user.result.RefreshResult
import com.jaeyong.oop.application.user.usecase.RefreshUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import com.jaeyong.oop.domain.user.port.RefreshTokenHandlerPort
import org.springframework.stereotype.Service

@Service
class RefreshService(
    private val jwtHandlerPort: JwtHandlerPort,
    private val refreshTokenHandlerPort: RefreshTokenHandlerPort
) : RefreshUseCase {

    override fun refresh(command: RefreshCommand): RefreshResult {
        val username = refreshTokenHandlerPort.validateAndExtractRefresh(TokenVO.from(command.refreshToken))
            ?: throw BaseException(ErrorCode.UNAUTHORIZED)
        val newAccess = jwtHandlerPort.generateToken(username)
        val newRefresh = refreshTokenHandlerPort.generateRefreshToken(username)
        return RefreshResult.of(newAccess.value, newRefresh.value)
    }
}
