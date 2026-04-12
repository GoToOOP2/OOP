package com.jaeyong.oop.infrastructure.user.entity

import com.jaeyong.oop.domain.user.User
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
    fun toDomain(): User = User(id, username, password)

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(user.id, user.username, user.password)
    }
}
