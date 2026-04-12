package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.common.TokenValidationCommand
import com.jaeyong.oop.application.user.result.TokenValidationResult

interface TokenValidationUseCase {
    fun validateAndExtract(command: TokenValidationCommand): TokenValidationResult
}
