package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.common.DeletePostCommand
import com.jaeyong.oop.application.post.common.GetPostCommand
import com.jaeyong.oop.application.post.common.UpdatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.PostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.domain.user.port.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Post 도메인의 UseCase 구현체.
 */
@Service
@Transactional(readOnly = true)
class PostService(
    private val postPort: PostPort,
    private val userQueryPort: UserQueryPort
) : PostUseCase {

    /**
     * 게시글을 생성하고 저장한다.
     *
     * @param command 게시글 생성에 필요한 제목, 본문, 작성자 ID
     * @return 저장된 게시글의 ID를 담은 결과
     */
    @Transactional
    override fun create(command: CreatePostCommand): CreatePostResult {
        val saved = Post.create(
            title = command.title,
            content = command.content,
            authorId = command.userId,
            postPort = postPort
        )
        return CreatePostResult.from(saved)
    }

    /**
     * ID로 게시글을 단건 조회한다.
     *
     * @param command 조회할 게시글 ID를 담은 커맨드
     * @return 게시글 상세 정보와 작성자명을 담은 결과
     * @throws BaseException 게시글 또는 작성자가 존재하지 않으면 [ErrorCode.NOT_FOUND]
     */
    override fun getById(command: GetPostCommand): GetPostResult {
        val post = postPort.getActiveById(command.postId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        val author = userQueryPort.findById(post.authorId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        return GetPostResult.from(post, author)
    }

    /**
     * 삭제되지 않은 모든 게시글을 목록으로 조회한다.
     *
     * @return 게시글 요약 정보 목록
     */
    override fun getAll(): List<GetPostListResult> {
        return postPort.getAllActive().map { post -> GetPostListResult.from(post) }
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
        val post = postPort.getActiveById(command.postId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        val author = userQueryPort.findById(post.authorId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        post.update(
            title = command.title,
            content = command.content,
            userId = command.userId
        )
        val saved = postPort.store(post)
        return UpdatePostResult.from(saved, author)
    }

    /**
     * 게시글을 소프트 삭제한다.
     *
     * @param command 삭제할 게시글 ID와 요청자 ID
     * @throws BaseException 게시글이 존재하지 않으면 [ErrorCode.NOT_FOUND]
     */
    @Transactional
    override fun delete(command: DeletePostCommand) {
        val post = postPort.getActiveById(command.postId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        post.delete(command.userId)
        postPort.store(post)
    }
}
