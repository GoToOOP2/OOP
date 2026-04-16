package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.common.DeletePostCommand
import com.jaeyong.oop.application.post.common.UpdatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.CreatePostUseCase
import com.jaeyong.oop.application.post.usecase.DeletePostUseCase
import com.jaeyong.oop.application.post.usecase.GetPostUseCase
import com.jaeyong.oop.application.post.usecase.UpdatePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.domain.post.vo.ContentVO
import com.jaeyong.oop.domain.post.vo.TitleVO
import com.jaeyong.oop.domain.user.port.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Post 도메인의 UseCase 통합 구현체.
 *
 * [CreatePostUseCase], [GetPostUseCase], [UpdatePostUseCase], [DeletePostUseCase]를 모두 구현한다.
 */
@Service
@Transactional(readOnly = true)
class PostService(
    private val postPort: PostPort,
    private val userQueryPort: UserQueryPort
) : CreatePostUseCase, GetPostUseCase, UpdatePostUseCase, DeletePostUseCase {

    /**
     * 게시글을 생성하고 저장한다.
     *
     * @param command 게시글 생성에 필요한 제목, 본문, 작성자 ID
     * @return 저장된 게시글의 ID를 담은 결과
     */
    @Transactional
    override fun create(command: CreatePostCommand): CreatePostResult {
        val post = Post.create(
            title = TitleVO.from(command.title),
            content = ContentVO.from(command.content),
            authorId = command.authorId
        )
        val saved = postPort.save(post)
        return CreatePostResult.of(requireNotNull(saved.id) { "Post ID must not be null after save" })
    }

    /**
     * ID로 게시글을 단건 조회한다.
     *
     * @param id 조회할 게시글 ID
     * @return 게시글 상세 정보와 작성자명을 담은 결과
     * @throws BaseException 게시글 또는 작성자가 존재하지 않으면 [ErrorCode.NOT_FOUND]
     */
    override fun getById(id: Long): GetPostResult {
        val post = postPort.findByIdAndDeletedFalse(id)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        val author = userQueryPort.findById(post.authorId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        return GetPostResult.of(
            id = requireNotNull(post.id) { "Post ID must not be null" },
            title = post.title.value,
            content = post.content.value,
            authorId = post.authorId,
            authorName = author.username.value,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )
    }

    /**
     * 삭제되지 않은 모든 게시글을 목록으로 조회한다.
     *
     * @return 게시글 요약 정보 목록
     */
    override fun getAll(): List<GetPostListResult> {
        val posts = postPort.findAllByDeletedFalse()
        val authorIds = posts.map { it.authorId }.distinct()
        val authorMap = userQueryPort.findByIds(authorIds)
        return posts.map { post ->
            GetPostListResult.of(
                id = requireNotNull(post.id) { "Post ID must not be null" },
                title = post.title.value,
                authorId = post.authorId,
                authorName = authorMap[post.authorId]?.username?.value ?: "알 수 없음",
                createdAt = post.createdAt
            )
        }
    }

    /**
     * 게시글의 제목과 본문을 수정한다.
     *
     * @param command 수정할 게시글 ID, 새 제목, 새 본문, 요청자 ID
     * @return 수정된 게시글 상세 정보와 작성자명을 담은 결과
     * @throws BaseException 게시글 또는 작성자가 존재하지 않으면 [ErrorCode.NOT_FOUND]
     */
    @Transactional
    override fun update(command: UpdatePostCommand): UpdatePostResult {
        val post = postPort.findByIdAndDeletedFalse(command.postId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        post.update(
            title = TitleVO.from(command.title),
            content = ContentVO.from(command.content),
            requesterId = command.requesterId
        )
        val saved = postPort.save(post)
        val author = userQueryPort.findById(saved.authorId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        return UpdatePostResult.of(
            id = requireNotNull(saved.id) { "Post ID must not be null after save" },
            title = saved.title.value,
            content = saved.content.value,
            authorId = saved.authorId,
            authorName = author.username.value,
            createdAt = saved.createdAt,
            updatedAt = saved.updatedAt
        )
    }

    /**
     * 게시글을 소프트 삭제한다.
     *
     * @param command 삭제할 게시글 ID와 요청자 ID
     * @throws BaseException 게시글이 존재하지 않으면 [ErrorCode.NOT_FOUND]
     */
    @Transactional
    override fun delete(command: DeletePostCommand) {
        val post = postPort.findByIdAndDeletedFalse(command.postId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        post.delete(command.requesterId)
        postPort.save(post)
    }
}
