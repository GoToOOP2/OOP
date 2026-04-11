package com.jaeyong.oop.infrastructure.user

import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.UserOutputPort
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserOutputPort {

    override fun register(user: User): User =
        userJpaRepository.save(UserEntity.fromDomain(user)).toDomain()

    override fun isUsernameTaken(username: String): Boolean =
        userJpaRepository.existsByUsername(username)

    override fun getByUsername(username: String): User? =
        userJpaRepository.findByUsername(username)?.toDomain()
}
