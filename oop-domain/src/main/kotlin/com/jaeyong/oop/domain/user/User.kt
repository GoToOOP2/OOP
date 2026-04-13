package com.jaeyong.oop.domain.user

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import com.jaeyong.oop.domain.user.port.UserPort
import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO
import com.jaeyong.oop.domain.user.vo.UsernameVO

// private constructor: 외부에서 직접 생성 금지. 반드시 아래 팩토리 메서드(signUp/login/restore)를 통해서만 생성
data class User private constructor(
    val id: Long? = null,
    val username: UsernameVO,
    val password: EncodedPasswordVO
) {
    // 비밀번호 검증 책임을 User가 직접 가짐 (TDA: 데이터를 가진 객체가 행동도 소유)
    // Service에서 비밀번호를 꺼내 비교하지 않고, User에게 검증을 위임
    fun authenticate(rawPassword: RawPasswordVO, passwordEncryptor: PasswordEncryptorPort) {
        if (!passwordEncryptor.matches(rawPassword, password)) {
            throw BaseException(ErrorCode.UNAUTHORIZED)
        }
    }

    companion object {
        // DB에서 조회한 데이터를 도메인 객체로 복원할 때 사용 (id 포함)
        // signUp/login과 달리 비즈니스 규칙 검증 없이 순수 복원만 수행
        fun restore(id: Long?, username: UsernameVO, password: EncodedPasswordVO): User = User(id, username, password)

        // 회원가입: 중복 검사 → 비밀번호 암호화 → User 생성
        // 비즈니스 규칙(중복 불가, 암호화 필수)을 도메인이 직접 강제
        fun signUp(username: UsernameVO, password: RawPasswordVO, passwordEncryptor: PasswordEncryptorPort, userPort: UserPort): User {
            if (userPort.isUsernameTaken(username)) {
                throw BaseException(ErrorCode.DUPLICATE)
            }
            return User(username = username, password = passwordEncryptor.encrypt(password))
        }

        // 로그인: 유저 조회 → 비밀번호 검증 → 인증된 User 반환
        // 조회 실패와 비밀번호 불일치 모두 UNAUTHORIZED로 처리 (어느 쪽인지 외부에 노출 안 함)
        fun login(username: UsernameVO, password: RawPasswordVO, userPort: UserPort, passwordEncryptor: PasswordEncryptorPort): User {
            val user = userPort.getByUsername(username)
                ?: throw BaseException(ErrorCode.UNAUTHORIZED)
            user.authenticate(password, passwordEncryptor)
            return user
        }
    }
}
