package com.jaeyong.oop.infrastructure.user.repository

import com.jaeyong.oop.infrastructure.user.entity.UserEntity

interface UserEntityRepository {

    /**
     * UserEntity를 저장하고 저장된 엔티티를 반환한다.
     *
     * @param entity 저장할 UserEntity
     * @return 저장된 UserEntity
     */
    fun save(entity: UserEntity): UserEntity

    /**
     * username 중복 여부를 확인한다.
     *
     * @param username 확인할 사용자명
     * @return 이미 존재하면 true
     */
    fun existsByUsername(username: String): Boolean

    /**
     * username으로 UserEntity를 조회한다.
     *
     * @param username 조회할 사용자명
     * @return 존재하면 UserEntity, 없으면 null
     */
    fun findByUsername(username: String): UserEntity?

    /**
     * id로 UserEntity를 조회한다.
     *
     * @param id 조회할 사용자 ID
     * @return 존재하면 UserEntity, 없으면 null
     */
    fun findUserById(id: Long): UserEntity?

    /**
     * id 목록으로 UserEntity를 일괄 조회한다.
     *
     * @param ids 조회할 사용자 ID 목록
     * @return 조회된 UserEntity 목록
     */
    fun findAllByIdIn(ids: List<Long>): List<UserEntity>
}
