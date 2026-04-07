package com.jaeyong.oop.infrastructure.user

import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {

    override fun save(user: User): User =
        userJpaRepository.save(UserEntity.fromDomain(user)).toDomain()

    override fun existsByUsername(username: String): Boolean =
        userJpaRepository.existsByUsername(username)
}
