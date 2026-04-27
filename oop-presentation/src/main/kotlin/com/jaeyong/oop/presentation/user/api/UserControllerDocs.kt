package com.jaeyong.oop.presentation.user.api

import com.jaeyong.oop.presentation.response.ApiResponse
import com.jaeyong.oop.presentation.user.request.JoinRequest
import com.jaeyong.oop.presentation.user.request.LoginRequest
import com.jaeyong.oop.presentation.user.request.RefreshRequest
import com.jaeyong.oop.presentation.user.response.TokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "User", description = "회원 인증 API")
interface UserControllerDocs {

    @Operation(summary = "내 정보 조회 (테스트용)", description = "JWT 토큰에서 추출한 현재 로그인 사용자의 username을 반환합니다.")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    )
    fun me(
        @Parameter(hidden = true) username: String?
    ): ResponseEntity<ApiResponse<String>>

    @Operation(summary = "회원가입", description = "username과 password를 받아 사용자를 등록합니다. username은 중복될 수 없습니다.")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "회원가입 성공"),
        SwaggerApiResponse(responseCode = "400", description = "요청 값 유효성 검사 실패 또는 username 중복")
    )
    fun join(request: JoinRequest): ResponseEntity<ApiResponse<Nothing>>

    @Operation(summary = "로그인", description = "username과 password로 인증 후 access token과 refresh token을 반환합니다.")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "로그인 성공, accessToken / refreshToken 반환"),
        SwaggerApiResponse(responseCode = "400", description = "자격증명 불일치")
    )
    fun login(request: LoginRequest): ResponseEntity<ApiResponse<TokenResponse>>

    @Operation(summary = "토큰 갱신", description = "refresh token을 검증하고 access / refresh token을 재발급합니다 (Rotate 방식).")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
        SwaggerApiResponse(responseCode = "400", description = "유효하지 않은 refresh token")
    )
    fun refresh(request: RefreshRequest): ResponseEntity<ApiResponse<TokenResponse>>
}
