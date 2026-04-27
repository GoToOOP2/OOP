package com.jaeyong.oop.infrastructure.user.entity

import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.vo.UsernameVO
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "username", nullable = false, unique = true, length = 50)
    val username: String,

    @Column(name = "password", nullable = false)
    val password: String
) {
    /**
     * DB 조회 결과를 도메인 객체로 변환한다.
     *
     * [User.restore]를 사용해 비즈니스 규칙 검증 없이 순수 복원만 수행한다.
     *
     * @return 복원된 [User] 도메인 객체
     */
    fun toDomain(): User = User.reconstruct(id, UsernameVO.from(username), EncodedPasswordVO.from(password))

    companion object {
        /**
         * 도메인 객체를 JPA 저장용 Entity로 변환한다.
         *
         * VO에서 raw value를 꺼내 String으로 저장한다.
         *
         * @param user 변환할 [User] 도메인 객체
         * @return 변환된 [UserEntity]
         */
        fun fromDomain(user: User): UserEntity = UserEntity(user.id, user.username.value, user.password.value)
    }
}
