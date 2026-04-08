package com.jaeyong.oop.application.service

import com.jaeyong.oop.domain.port.JwtPort
import com.jaeyong.oop.domain.port.PasswordEncodePort
import com.jaeyong.oop.domain.port.RefreshTokenPort
import com.jaeyong.oop.application.command.LoginCommand
import com.jaeyong.oop.application.command.SignupCommand
import com.jaeyong.oop.application.result.TokenResult
import com.jaeyong.oop.application.usecase.AuthUseCase
import com.jaeyong.oop.application.usecase.TokenValidationUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.member.Member
import com.jaeyong.oop.domain.member.port.MemberPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 인증 비즈니스 로직을 담당하는 서비스.
 * AuthUseCase(회원가입/로그인/재발급/로그아웃)와
 * TokenValidationUseCase(토큰 검증/memberId 추출)를 모두 구현한다.
 *
 * 이 서비스는 직접 DB나 JWT를 다루지 않고,
 * 도메인이 정의한 아웃바운드 포트(MemberPort, JwtPort, PasswordEncodePort, RefreshTokenPort)를
 * 통해 인프라 레이어에 위임한다.
 */
@Service
class AuthService(
    private val memberPort: MemberPort,              // 회원 저장소 (→ MemberPersistenceAdapter)
    private val jwtPort: JwtPort,                    // JWT 생성/검증 (→ JwtAdapter)
    private val passwordEncodePort: PasswordEncodePort, // 비밀번호 해싱 (→ BcryptAdapter)
    private val refreshTokenPort: RefreshTokenPort,  // Refresh Token 저장소 (→ RefreshTokenPersistenceAdapter)
) : AuthUseCase, TokenValidationUseCase {

    /**
     * 회원가입 처리.
     * 1. 이메일 중복 검사 → 이미 존재하면 DUPLICATE_EMAIL 예외
     * 2. 평문 비밀번호를 BCrypt로 해싱
     * 3. 회원 정보를 DB에 저장
     */
    @Transactional
    override fun signup(command: SignupCommand) {
        if (memberPort.existsByEmail(command.email)) {
            throw BaseException(ErrorCode.DUPLICATE_EMAIL)
        }

        val hashedPassword = passwordEncodePort.encode(command.password)
        val member =
            Member(
                email = command.email,
                password = hashedPassword,
                nickname = command.nickname,
            )
        memberPort.save(member)
    }

    /**
     * 로그인 처리.
     * 1. 이메일로 회원 조회 → 없으면 LOGIN_FAILED 예외
     * 2. 비밀번호 검증 → 틀리면 LOGIN_FAILED 예외
     *    (이메일 없음/비밀번호 틀림을 같은 에러코드로 반환 → 보안상 어떤 정보가 틀렸는지 노출하지 않음)
     * 3. Access Token(30분) + Refresh Token(14일) 발급
     * 4. Refresh Token을 해싱하여 DB에 저장 (평문 저장 안 함)
     */
    @Transactional
    override fun login(command: LoginCommand): TokenResult {
        val member =
            memberPort.findByEmail(command.email)
                ?: throw BaseException(ErrorCode.LOGIN_FAILED)

        if (!passwordEncodePort.matches(command.password, member.password)) {
            throw BaseException(ErrorCode.LOGIN_FAILED)
        }

        val memberId = member.id!!
        val accessToken = jwtPort.createAccessToken(memberId)
        val refreshToken = jwtPort.createRefreshToken(memberId)

        // Refresh Token은 해싱해서 저장 — DB가 유출되어도 토큰 원본을 알 수 없다
        refreshTokenPort.save(memberId, passwordEncodePort.encode(refreshToken))

        return TokenResult(accessToken, refreshToken)
    }

    /**
     * 토큰 재발급 (Refresh Token Rotation 방식).
     * 1. 전달받은 Refresh Token의 유효성 검증 (만료/변조 체크)
     * 2. 토큰에서 memberId 추출
     * 3. DB에 저장된 해싱 Refresh Token과 비교 → 불일치 시 INVALID_TOKEN 예외
     * 4. 새 Access Token + 새 Refresh Token 발급
     * 5. 새 Refresh Token을 해싱하여 DB 갱신 (이전 Refresh Token은 더 이상 사용 불가)
     */
    @Transactional
    override fun reissue(refreshToken: String): TokenResult {
        jwtPort.validateToken(refreshToken)

        val memberId = jwtPort.extractMemberId(refreshToken)
        val storedHashedToken = refreshTokenPort.findByMemberId(memberId)
                ?: throw BaseException(ErrorCode.INVALID_TOKEN)

        if (!passwordEncodePort.matches(refreshToken, storedHashedToken)) {
            throw BaseException(ErrorCode.INVALID_TOKEN)
        }

        val newAccessToken = jwtPort.createAccessToken(memberId)
        val newRefreshToken = jwtPort.createRefreshToken(memberId)

        refreshTokenPort.save(memberId, passwordEncodePort.encode(newRefreshToken))

        return TokenResult(newAccessToken, newRefreshToken)
    }

    /**
     * 로그아웃 처리.
     * DB에서 해당 회원의 Refresh Token을 삭제한다.
     * 이후 클라이언트가 재발급을 요청해도 저장된 토큰이 없으므로 거부된다.
     * (Access Token은 만료될 때까지 유효하지만, 짧은 수명(30분)으로 리스크를 최소화)
     */
    @Transactional
    override fun logout(memberId: Long) {
        refreshTokenPort.deleteByMemberId(memberId)
    }

    // ── TokenValidationUseCase 구현 ──
    // JwtAuthenticationInterceptor(presentation)가 domain의 JwtPort를 직접 의존하지 않도록
    // application 레이어에서 중간 다리 역할을 한다.
    override fun validateToken(token: String) {
        jwtPort.validateToken(token)
    }

    override fun extractMemberId(token: String): Long {
        return jwtPort.extractMemberId(token)
    }
}
