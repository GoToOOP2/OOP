package com.jaeyong.oop.infrastructure.user

import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.UserOutputPort
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userRepository: UserRepository
) : UserOutputPort {

    override fun register(user: User): User =
        userRepository.save(UserEntity.fromDomain(user)).toDomain()

    override fun isUsernameTaken(username: String): Boolean =
        userRepository.existsByUsername(username)

    override fun getByUsername(username: String): User? =
        userRepository.findByUsername(username)?.toDomain()
}
