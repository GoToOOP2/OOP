package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.RefreshCommand
import com.jaeyong.oop.application.user.result.RefreshResult
import com.jaeyong.oop.application.user.usecase.RefreshUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import com.jaeyong.oop.domain.user.port.RefreshTokenHandlerPort
import com.jaeyong.oop.domain.user.port.UserQueryPort
import org.springframework.stereotype.Service

@Service
class RefreshService(
    private val jwtHandlerPort: JwtHandlerPort,
    private val refreshTokenHandlerPort: RefreshTokenHandlerPort,
    private val userQueryPort: UserQueryPort
) : RefreshUseCase {

    override fun refresh(command: RefreshCommand): RefreshResult {
        val userId = refreshTokenHandlerPort.validateAndExtractRefresh(TokenVO.from(command.refreshToken))
            ?: throw BaseException(ErrorCode.UNAUTHORIZED)
        val user = userQueryPort.findById(userId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        val accessToken = jwtHandlerPort.generateToken(user.username, user.id!!)
        val newRefreshToken = refreshTokenHandlerPort.generateRefreshToken(user.username, user.id!!)
        return RefreshResult.of(accessToken.value, newRefreshToken.value)
    }
}
