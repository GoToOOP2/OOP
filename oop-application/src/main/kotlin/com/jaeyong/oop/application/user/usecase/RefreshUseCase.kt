package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.common.RefreshCommand
import com.jaeyong.oop.application.user.result.RefreshResult

interface RefreshUseCase {
    fun refresh(command: RefreshCommand): RefreshResult
}
