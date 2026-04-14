package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.common.TokenValidationCommand
import com.jaeyong.oop.application.user.result.TokenValidationResult

interface TokenValidationUseCase {

    /**
     * Access token을 검증하고 사용자명을 추출한다.
     *
     * @param command 검증할 access token을 담은 요청
     * @return 유효하면 사용자명, 유효하지 않으면 username이 null인 결과
     */
    fun validateAndExtract(command: TokenValidationCommand): TokenValidationResult
}
