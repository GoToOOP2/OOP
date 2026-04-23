package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.User

interface UserQueryPort {

    /**
     * 사용자 ID로 사용자를 조회한다.
     *
     * @param id 사용자 ID
     * @return 사용자가 존재하면 User, 없으면 null
     */
    fun findById(id: Long): User?

    /**
     * 사용자 ID 목록으로 사용자를 일괄 조회한다.
     *
     * @param ids 조회할 사용자 ID 목록
     * @return ID를 키로 하는 사용자 Map
     */
    fun findByIds(ids: List<Long>): Map<Long, User>
}
