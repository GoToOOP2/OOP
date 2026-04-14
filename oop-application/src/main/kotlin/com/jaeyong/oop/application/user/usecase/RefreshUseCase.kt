package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.common.RefreshCommand
import com.jaeyong.oop.application.user.result.RefreshResult

interface RefreshUseCase {

    /**
     * Refresh token을 검증하고 access/refresh token을 재발급한다 (Rotate 전략).
     *
     * @param command refresh token을 담은 갱신 요청
     * @return 새로 발급된 access token과 refresh token
     * @throws com.jaeyong.oop.common.exception.BaseException refresh token이 유효하지 않으면 (UNAUTHORIZED)
     */
    fun refresh(command: RefreshCommand): RefreshResult
}
