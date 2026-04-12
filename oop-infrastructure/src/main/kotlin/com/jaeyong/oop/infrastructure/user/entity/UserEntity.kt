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
    fun toDomain(): User = User(id, UsernameVO.from(username), EncodedPasswordVO.from(password))

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(user.id, user.username.value, user.password.value)
    }
}
