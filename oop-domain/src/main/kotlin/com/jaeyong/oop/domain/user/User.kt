package com.jaeyong.oop.domain.user

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import com.jaeyong.oop.domain.user.port.UserPort
import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO
import com.jaeyong.oop.domain.user.vo.UsernameVO

/**
 * 사용자 도메인 객체.
 *
 * 외부에서 직접 생성 금지 — 반드시 [signUp], [login], [reconstruct] 팩토리 메서드를 통해 생성한다.
 */
data class User private constructor(
    val id: Long? = null,
    val username: UsernameVO,
    val password: EncodedPasswordVO
) {

    /**
     * 비밀번호를 검증한다.
     *
     * TDA 원칙에 따라 비밀번호 검증 책임을 User가 직접 소유한다.
     * Service에서 password를 꺼내 비교하지 않고 User에게 위임한다.
     *
     * @param rawPassword 검증할 평문 비밀번호
     * @param passwordEncryptor 비밀번호 비교에 사용할 암호화 포트
     * @throws BaseException 비밀번호 불일치 시 (UNAUTHORIZED)
     */
    fun authenticate(rawPassword: RawPasswordVO, passwordEncryptor: PasswordEncryptorPort) {
        if (!passwordEncryptor.matches(rawPassword, password)) {
            throw BaseException(ErrorCode.UNAUTHORIZED)
        }
    }

    companion object {

        /**
         * DB에서 조회한 데이터로 User를 재구성한다.
         *
         * [signUp]·[login]과 달리 비즈니스 규칙 검증 없이 순수 재구성만 수행한다.
         * 영속성 계층(Entity → Domain 변환)에서만 사용한다.
         *
         * @param id DB에서 할당된 식별자
         * @param username 사용자명 VO
         * @param password 암호화된 비밀번호 VO
         */
        fun reconstruct(id: Long?, username: UsernameVO, password: EncodedPasswordVO): User = User(id, username, password)

        /**
         * 회원가입 — 중복 검사 후 비밀번호를 암호화하여 User를 생성한다.
         *
         * 중복 불가·암호화 필수 규칙을 도메인이 직접 강제한다.
         *
         * @param username 가입할 사용자명
         * @param password 가입할 평문 비밀번호
         * @param passwordEncryptor 비밀번호 암호화 포트
         * @param userPort 중복 검사에 사용할 사용자 저장소 포트
         * @return 암호화된 비밀번호를 가진 신규 User
         * @throws BaseException username 중복 시 (DUPLICATE)
         */
        fun signUp(username: UsernameVO, password: RawPasswordVO, passwordEncryptor: PasswordEncryptorPort, userPort: UserPort): User {
            if (userPort.isUsernameTaken(username)) {
                throw BaseException(ErrorCode.DUPLICATE)
            }
            return User(username = username, password = passwordEncryptor.encrypt(password))
        }

        /**
         * 로그인 — 사용자를 조회하고 비밀번호를 검증하여 인증된 User를 반환한다.
         *
         * 조회 실패와 비밀번호 불일치 모두 UNAUTHORIZED로 처리한다.
         * 어느 쪽이 실패했는지 외부에 노출하지 않아 열거 공격(enumeration attack)을 방지한다.
         *
         * @param username 로그인할 사용자명
         * @param password 로그인할 평문 비밀번호
         * @param userPort 사용자 조회에 사용할 저장소 포트
         * @param passwordEncryptor 비밀번호 검증에 사용할 암호화 포트
         * @return 인증된 User
         * @throws BaseException 사용자 미존재 또는 비밀번호 불일치 시 (UNAUTHORIZED)
         */
        fun login(username: UsernameVO, password: RawPasswordVO, userPort: UserPort, passwordEncryptor: PasswordEncryptorPort): User {
            val user = userPort.getByUsername(username)
                ?: throw BaseException(ErrorCode.UNAUTHORIZED)
            user.authenticate(password, passwordEncryptor)
            return user
        }
    }
}
