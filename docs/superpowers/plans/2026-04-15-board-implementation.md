# Board 게시판 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** BOARD.md 설계 기반으로 게시판 CRUD 기능을 헥사고날 아키텍처로 구현한다.

**Architecture:** 4계층 헥사고날 아키텍처 (domain → application → infrastructure ← presentation). 도메인이 비즈니스 규칙을 직접 소유 (TDA), Spring/JPA는 infrastructure에만 존재.

**Tech Stack:** Kotlin, Spring Boot 3, JPA, PostgreSQL, JUnit5, Mockito

---

## 파일 구조

### 신규 생성

```
oop-common
└── src/main/kotlin/com/jaeyong/oop/common/exception/ErrorCode.kt  (수정)

oop-domain
└── src/main/kotlin/com/jaeyong/oop/domain/post/
    ├── Post.kt
    ├── PostTitle.kt
    ├── PostContent.kt
    └── port/
        └── PostPort.kt
└── src/test/kotlin/com/jaeyong/oop/domain/post/
    ├── PostTest.kt
    ├── PostTitleTest.kt
    └── PostContentTest.kt

oop-application
└── src/main/kotlin/com/jaeyong/oop/application/post/
    ├── command/
    │   ├── CreatePostCommand.kt
    │   ├── GetPostCommand.kt
    │   ├── GetPostListCommand.kt
    │   ├── UpdatePostCommand.kt
    │   └── DeletePostCommand.kt
    ├── result/
    │   ├── CreatePostResult.kt
    │   ├── GetPostResult.kt
    │   ├── GetPostListResult.kt
    │   └── UpdatePostResult.kt
    ├── usecase/
    │   ├── CreatePostUseCase.kt
    │   ├── GetPostUseCase.kt
    │   ├── GetPostListUseCase.kt
    │   ├── UpdatePostUseCase.kt
    │   └── DeletePostUseCase.kt
    └── service/
        ├── CreatePostService.kt
        ├── GetPostService.kt
        ├── GetPostListService.kt
        ├── UpdatePostService.kt
        └── DeletePostService.kt
└── src/test/kotlin/com/jaeyong/oop/application/post/service/
    ├── CreatePostServiceTest.kt
    ├── GetPostServiceTest.kt
    ├── GetPostListServiceTest.kt
    ├── UpdatePostServiceTest.kt
    └── DeletePostServiceTest.kt

oop-infrastructure
└── src/main/kotlin/com/jaeyong/oop/infrastructure/post/
    ├── entity/
    │   └── PostEntity.kt
    ├── repository/
    │   └── PostEntityRepository.kt
    ├── jpa/
    │   └── PostJpaRepository.kt
    └── adapter/
        └── PostPersistenceAdapter.kt
└── src/test/kotlin/com/jaeyong/oop/infrastructure/post/
    ├── entity/PostEntityTest.kt
    └── adapter/PostPersistenceAdapterTest.kt

oop-presentation
└── src/main/kotlin/com/jaeyong/oop/presentation/post/
    ├── request/
    │   ├── CreatePostRequest.kt
    │   └── UpdatePostRequest.kt
    ├── response/
    │   ├── PostResponse.kt
    │   └── PostListResponse.kt
    └── api/
        └── PostController.kt
└── src/test/kotlin/com/jaeyong/oop/presentation/post/api/
    └── PostControllerTest.kt
```

---

## Task 1: ErrorCode 추가

**Files:**
- Modify: `oop-common/src/main/kotlin/com/jaeyong/oop/common/exception/ErrorCode.kt`

- [ ] **Step 1: POST_ACCESS_DENIED 에러코드 추가**

```kotlin
// BUSINESS_RULE_VIOLATION("D003") 아래에 추가
POST_ACCESS_DENIED("D005"),
```

- [ ] **Step 2: 빌드 확인**

```bash
./gradlew :oop-common:build
```

- [ ] **Step 3: 커밋**

```bash
git add oop-common/src/main/kotlin/com/jaeyong/oop/common/exception/ErrorCode.kt
git commit -m "feat(common): POST_ACCESS_DENIED 에러코드 추가"
```

---

## Task 2: 도메인 구현 — PostTitle, PostContent (VO)

**Files:**
- Create: `oop-domain/src/main/kotlin/com/jaeyong/oop/domain/post/PostTitle.kt`
- Create: `oop-domain/src/main/kotlin/com/jaeyong/oop/domain/post/PostContent.kt`
- Create: `oop-domain/src/test/kotlin/com/jaeyong/oop/domain/post/PostTitleTest.kt`
- Create: `oop-domain/src/test/kotlin/com/jaeyong/oop/domain/post/PostContentTest.kt`

- [ ] **Step 1: PostTitleTest 작성**

```kotlin
package com.jaeyong.oop.domain.post

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PostTitleTest {

    @Test
    fun `정상적인 제목으로 PostTitle을 생성한다`() {
        val title = PostTitle.of("게시글 제목")
        assertThat(title.value).isEqualTo("게시글 제목")
    }

    @Test
    fun `제목이 공백이면 예외를 던진다`() {
        assertThrows<IllegalArgumentException> { PostTitle.of("   ") }
    }

    @Test
    fun `제목이 100자를 초과하면 예외를 던진다`() {
        assertThrows<IllegalArgumentException> { PostTitle.of("a".repeat(101)) }
    }

    @Test
    fun `제목이 정확히 100자이면 생성된다`() {
        val title = PostTitle.of("a".repeat(100))
        assertThat(title.value).hasSize(100)
    }
}
```

- [ ] **Step 2: PostContentTest 작성**

```kotlin
package com.jaeyong.oop.domain.post

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PostContentTest {

    @Test
    fun `정상적인 내용으로 PostContent를 생성한다`() {
        val content = PostContent.of("게시글 내용")
        assertThat(content.value).isEqualTo("게시글 내용")
    }

    @Test
    fun `내용이 공백이면 예외를 던진다`() {
        assertThrows<IllegalArgumentException> { PostContent.of("   ") }
    }

    @Test
    fun `내용이 5000자를 초과하면 예외를 던진다`() {
        assertThrows<IllegalArgumentException> { PostContent.of("a".repeat(5001)) }
    }

    @Test
    fun `내용이 정확히 5000자이면 생성된다`() {
        val content = PostContent.of("a".repeat(5000))
        assertThat(content.value).hasSize(5000)
    }
}
```

- [ ] **Step 3: 테스트 실패 확인**

```bash
./gradlew :oop-domain:test --tests "com.jaeyong.oop.domain.post.PostTitleTest" --tests "com.jaeyong.oop.domain.post.PostContentTest"
```

Expected: FAIL (클래스 없음)

- [ ] **Step 4: PostTitle 구현**

```kotlin
package com.jaeyong.oop.domain.post

data class PostTitle private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 100
        fun of(value: String): PostTitle {
            require(value.isNotBlank()) { "제목은 공백일 수 없습니다" }
            require(value.length <= MAX_LENGTH) { "제목은 ${MAX_LENGTH}자 이하여야 합니다" }
            return PostTitle(value)
        }
    }
}
```

- [ ] **Step 5: PostContent 구현**

```kotlin
package com.jaeyong.oop.domain.post

data class PostContent private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 5000
        fun of(value: String): PostContent {
            require(value.isNotBlank()) { "내용은 공백일 수 없습니다" }
            require(value.length <= MAX_LENGTH) { "내용은 ${MAX_LENGTH}자 이하여야 합니다" }
            return PostContent(value)
        }
    }
}
```

- [ ] **Step 6: 테스트 통과 확인**

```bash
./gradlew :oop-domain:test --tests "com.jaeyong.oop.domain.post.PostTitleTest" --tests "com.jaeyong.oop.domain.post.PostContentTest"
```

Expected: PASS

- [ ] **Step 7: 커밋**

```bash
git add oop-domain/src/main/kotlin/com/jaeyong/oop/domain/post/PostTitle.kt
git add oop-domain/src/main/kotlin/com/jaeyong/oop/domain/post/PostContent.kt
git add oop-domain/src/test/kotlin/com/jaeyong/oop/domain/post/PostTitleTest.kt
git add oop-domain/src/test/kotlin/com/jaeyong/oop/domain/post/PostContentTest.kt
git commit -m "feat(domain): PostTitle, PostContent VO 구현"
```

---

## Task 3: 도메인 구현 — Post Entity, PostPort

**Files:**
- Create: `oop-domain/src/main/kotlin/com/jaeyong/oop/domain/post/Post.kt`
- Create: `oop-domain/src/main/kotlin/com/jaeyong/oop/domain/post/port/PostPort.kt`
- Create: `oop-domain/src/test/kotlin/com/jaeyong/oop/domain/post/PostTest.kt`

- [ ] **Step 1: PostTest 작성**

```kotlin
package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class PostTest {

    private val title = PostTitle.of("제목")
    private val content = PostContent.of("내용")
    private val author = "user1"

    @Test
    fun `create로 새 게시글을 생성한다`() {
        val post = Post.create(title, content, author)

        assertThat(post.id).isNull()
        assertThat(post.title).isEqualTo(title)
        assertThat(post.content).isEqualTo(content)
        assertThat(post.authorUsername).isEqualTo(author)
        assertThat(post.updatedAt).isNull()
    }

    @Test
    fun `restore로 기존 게시글을 복원한다`() {
        val createdAt = LocalDateTime.now()
        val post = Post.restore(1L, title, content, author, createdAt, null)

        assertThat(post.id).isEqualTo(1L)
        assertThat(post.createdAt).isEqualTo(createdAt)
    }

    @Test
    fun `작성자가 수정하면 title과 content가 변경된다`() {
        val post = Post.create(title, content, author)
        val newTitle = PostTitle.of("새 제목")
        val newContent = PostContent.of("새 내용")

        val updated = post.update(newTitle, newContent, author)

        assertThat(updated.title).isEqualTo(newTitle)
        assertThat(updated.content).isEqualTo(newContent)
        assertThat(updated.updatedAt).isNotNull()
    }

    @Test
    fun `작성자가 아닌 사람이 수정하면 POST_ACCESS_DENIED 예외를 던진다`() {
        val post = Post.create(title, content, author)

        val exception = assertThrows<BaseException> {
            post.update(PostTitle.of("새 제목"), PostContent.of("새 내용"), "other")
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.POST_ACCESS_DENIED)
    }

    @Test
    fun `작성자가 아닌 사람이 validateOwner 호출하면 POST_ACCESS_DENIED 예외를 던진다`() {
        val post = Post.create(title, content, author)

        val exception = assertThrows<BaseException> { post.validateOwner("other") }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.POST_ACCESS_DENIED)
    }

    @Test
    fun `작성자가 validateOwner 호출하면 예외를 던지지 않는다`() {
        val post = Post.create(title, content, author)
        post.validateOwner(author)
    }
}
```

- [ ] **Step 2: 테스트 실패 확인**

```bash
./gradlew :oop-domain:test --tests "com.jaeyong.oop.domain.post.PostTest"
```

Expected: FAIL (클래스 없음)

- [ ] **Step 3: Post 구현**

```kotlin
package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import java.time.LocalDateTime

data class Post private constructor(
    val id: Long? = null,
    val title: PostTitle,
    val content: PostContent,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null
) {
    fun validateOwner(requesterUsername: String) {
        if (authorUsername != requesterUsername)
            throw BaseException(ErrorCode.POST_ACCESS_DENIED)
    }

    fun update(title: PostTitle, content: PostContent, requesterUsername: String): Post {
        validateOwner(requesterUsername)
        return copy(title = title, content = content, updatedAt = LocalDateTime.now())
    }

    companion object {
        fun create(title: PostTitle, content: PostContent, authorUsername: String): Post =
            Post(title = title, content = content, authorUsername = authorUsername, createdAt = LocalDateTime.now())

        fun restore(
            id: Long?,
            title: PostTitle,
            content: PostContent,
            authorUsername: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime?
        ): Post = Post(id, title, content, authorUsername, createdAt, updatedAt)
    }
}
```

- [ ] **Step 4: PostPort 구현**

```kotlin
package com.jaeyong.oop.domain.post.port

import com.jaeyong.oop.domain.post.Post

interface PostPort {
    fun save(post: Post): Post
    fun findById(id: Long): Post?
    fun findAll(page: Int, size: Int): List<Post>
    fun countAll(): Long
    fun deleteById(id: Long)
}
```

- [ ] **Step 5: 테스트 통과 확인**

```bash
./gradlew :oop-domain:test --tests "com.jaeyong.oop.domain.post.PostTest"
```

Expected: PASS

- [ ] **Step 6: 커밋**

```bash
git add oop-domain/src/main/kotlin/com/jaeyong/oop/domain/post/Post.kt
git add oop-domain/src/main/kotlin/com/jaeyong/oop/domain/post/port/PostPort.kt
git add oop-domain/src/test/kotlin/com/jaeyong/oop/domain/post/PostTest.kt
git commit -m "feat(domain): Post 엔티티 및 PostPort 구현"
```

---

## Task 4: Application 계층 구현

**Files:**
- Create: `oop-application/src/main/kotlin/com/jaeyong/oop/application/post/command/*.kt` (5개)
- Create: `oop-application/src/main/kotlin/com/jaeyong/oop/application/post/result/*.kt` (4개)
- Create: `oop-application/src/main/kotlin/com/jaeyong/oop/application/post/usecase/*.kt` (5개)
- Create: `oop-application/src/main/kotlin/com/jaeyong/oop/application/post/service/*.kt` (5개)
- Create: `oop-application/src/test/kotlin/com/jaeyong/oop/application/post/service/*.kt` (5개)

- [ ] **Step 1: Command 클래스 5개 생성**

```kotlin
// CreatePostCommand.kt
package com.jaeyong.oop.application.post.command
data class CreatePostCommand private constructor(
    val title: String, val content: String, val authorUsername: String
) {
    companion object {
        fun of(title: String, content: String, authorUsername: String) =
            CreatePostCommand(title, content, authorUsername)
    }
}

// GetPostCommand.kt
package com.jaeyong.oop.application.post.command
data class GetPostCommand private constructor(val id: Long) {
    companion object { fun of(id: Long) = GetPostCommand(id) }
}

// GetPostListCommand.kt
package com.jaeyong.oop.application.post.command
data class GetPostListCommand private constructor(val page: Int, val size: Int) {
    companion object { fun of(page: Int, size: Int) = GetPostListCommand(page, size) }
}

// UpdatePostCommand.kt
package com.jaeyong.oop.application.post.command
data class UpdatePostCommand private constructor(
    val id: Long, val title: String, val content: String, val requesterUsername: String
) {
    companion object {
        fun of(id: Long, title: String, content: String, requesterUsername: String) =
            UpdatePostCommand(id, title, content, requesterUsername)
    }
}

// DeletePostCommand.kt
package com.jaeyong.oop.application.post.command
data class DeletePostCommand private constructor(val id: Long, val requesterUsername: String) {
    companion object { fun of(id: Long, requesterUsername: String) = DeletePostCommand(id, requesterUsername) }
}
```

- [ ] **Step 2: Result 클래스 4개 생성**

```kotlin
// CreatePostResult.kt
package com.jaeyong.oop.application.post.result
import com.jaeyong.oop.domain.post.Post
import java.time.LocalDateTime
data class CreatePostResult private constructor(
    val id: Long, val title: String, val content: String,
    val authorUsername: String, val createdAt: LocalDateTime
) {
    companion object {
        fun of(post: Post) = CreatePostResult(
            post.id!!, post.title.value, post.content.value, post.authorUsername, post.createdAt
        )
    }
}

// GetPostResult.kt
package com.jaeyong.oop.application.post.result
import com.jaeyong.oop.domain.post.Post
import java.time.LocalDateTime
data class GetPostResult private constructor(
    val id: Long, val title: String, val content: String,
    val authorUsername: String, val createdAt: LocalDateTime, val updatedAt: LocalDateTime?
) {
    companion object {
        fun of(post: Post) = GetPostResult(
            post.id!!, post.title.value, post.content.value,
            post.authorUsername, post.createdAt, post.updatedAt
        )
    }
}

// GetPostListResult.kt
package com.jaeyong.oop.application.post.result
import com.jaeyong.oop.domain.post.Post
data class GetPostListResult private constructor(
    val posts: List<GetPostResult>, val totalCount: Long, val page: Int, val size: Int
) {
    companion object {
        fun of(posts: List<Post>, totalCount: Long, page: Int, size: Int) =
            GetPostListResult(posts.map { GetPostResult.of(it) }, totalCount, page, size)
    }
}

// UpdatePostResult.kt
package com.jaeyong.oop.application.post.result
import com.jaeyong.oop.domain.post.Post
import java.time.LocalDateTime
data class UpdatePostResult private constructor(
    val id: Long, val title: String, val content: String, val updatedAt: LocalDateTime?
) {
    companion object {
        fun of(post: Post) = UpdatePostResult(post.id!!, post.title.value, post.content.value, post.updatedAt)
    }
}
```

- [ ] **Step 3: UseCase 인터페이스 5개 생성**

```kotlin
// CreatePostUseCase.kt
package com.jaeyong.oop.application.post.usecase
import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
interface CreatePostUseCase { fun create(command: CreatePostCommand): CreatePostResult }

// GetPostUseCase.kt
package com.jaeyong.oop.application.post.usecase
import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.application.post.result.GetPostResult
interface GetPostUseCase { fun get(command: GetPostCommand): GetPostResult }

// GetPostListUseCase.kt
package com.jaeyong.oop.application.post.usecase
import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.application.post.result.GetPostListResult
interface GetPostListUseCase { fun getList(command: GetPostListCommand): GetPostListResult }

// UpdatePostUseCase.kt
package com.jaeyong.oop.application.post.usecase
import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.result.UpdatePostResult
interface UpdatePostUseCase { fun update(command: UpdatePostCommand): UpdatePostResult }

// DeletePostUseCase.kt
package com.jaeyong.oop.application.post.usecase
import com.jaeyong.oop.application.post.command.DeletePostCommand
interface DeletePostUseCase { fun delete(command: DeletePostCommand) }
```

- [ ] **Step 4: Service 테스트 5개 작성**

```kotlin
// CreatePostServiceTest.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class CreatePostServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: CreatePostService

    @Test
    fun `게시글을 작성하면 저장된 결과를 반환한다`() {
        val command = CreatePostCommand.of("제목", "내용", "user1")
        val saved = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.save(any())).willReturn(saved)

        val result = sut.create(command)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("제목")
        assertThat(result.authorUsername).isEqualTo("user1")
    }
}

// GetPostServiceTest.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class GetPostServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: GetPostService

    @Test
    fun `존재하는 게시글을 조회하면 결과를 반환한다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.findById(1L)).willReturn(post)

        val result = sut.get(GetPostCommand.of(1L))

        assertThat(result.id).isEqualTo(1L)
    }

    @Test
    fun `존재하지 않는 게시글 조회 시 NOT_FOUND 예외를 던진다`() {
        given(postPort.findById(999L)).willReturn(null)

        val exception = assertThrows<BaseException> { sut.get(GetPostCommand.of(999L)) }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
    }
}

// GetPostListServiceTest.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class GetPostListServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: GetPostListService

    @Test
    fun `게시글 목록을 페이지 단위로 조회한다`() {
        val posts = listOf(
            Post.restore(1L, PostTitle.of("제목1"), PostContent.of("내용1"), "user1", LocalDateTime.now(), null)
        )
        given(postPort.findAll(0, 20)).willReturn(posts)
        given(postPort.countAll()).willReturn(1L)

        val result = sut.getList(GetPostListCommand.of(0, 20))

        assertThat(result.posts).hasSize(1)
        assertThat(result.totalCount).isEqualTo(1L)
    }
}

// UpdatePostServiceTest.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UpdatePostServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: UpdatePostService

    @Test
    fun `작성자가 수정하면 수정된 결과를 반환한다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        val updated = Post.restore(1L, PostTitle.of("새제목"), PostContent.of("새내용"), "user1", post.createdAt, LocalDateTime.now())
        given(postPort.findById(1L)).willReturn(post)
        given(postPort.save(any())).willReturn(updated)

        val result = sut.update(UpdatePostCommand.of(1L, "새제목", "새내용", "user1"))

        assertThat(result.title).isEqualTo("새제목")
        assertThat(result.updatedAt).isNotNull()
    }

    @Test
    fun `존재하지 않는 게시글 수정 시 NOT_FOUND 예외를 던진다`() {
        given(postPort.findById(999L)).willReturn(null)

        val exception = assertThrows<BaseException> {
            sut.update(UpdatePostCommand.of(999L, "제목", "내용", "user1"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
    }
}

// DeletePostServiceTest.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class DeletePostServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: DeletePostService

    @Test
    fun `작성자가 삭제하면 성공한다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.findById(1L)).willReturn(post)

        sut.delete(DeletePostCommand.of(1L, "user1"))
    }

    @Test
    fun `존재하지 않는 게시글 삭제 시 NOT_FOUND 예외를 던진다`() {
        given(postPort.findById(999L)).willReturn(null)

        val exception = assertThrows<BaseException> {
            sut.delete(DeletePostCommand.of(999L, "user1"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
    }
}
```

- [ ] **Step 5: 테스트 실패 확인**

```bash
./gradlew :oop-application:compileTestKotlin
```

Expected: FAIL (Service 클래스 없음)

- [ ] **Step 6: Service 5개 구현**

```kotlin
// CreatePostService.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.usecase.CreatePostUseCase
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePostService(private val postPort: PostPort) : CreatePostUseCase {
    @Transactional
    override fun create(command: CreatePostCommand): CreatePostResult {
        val post = Post.create(
            title = PostTitle.of(command.title),
            content = PostContent.of(command.content),
            authorUsername = command.authorUsername
        )
        return CreatePostResult.of(postPort.save(post))
    }
}

// GetPostService.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.usecase.GetPostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPostService(private val postPort: PostPort) : GetPostUseCase {
    @Transactional(readOnly = true)
    override fun get(command: GetPostCommand): GetPostResult {
        val post = postPort.findById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        return GetPostResult.of(post)
    }
}

// GetPostListService.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.usecase.GetPostListUseCase
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPostListService(private val postPort: PostPort) : GetPostListUseCase {
    @Transactional(readOnly = true)
    override fun getList(command: GetPostListCommand): GetPostListResult {
        val posts = postPort.findAll(command.page, command.size)
        val total = postPort.countAll()
        return GetPostListResult.of(posts, total, command.page, command.size)
    }
}

// UpdatePostService.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.UpdatePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePostService(private val postPort: PostPort) : UpdatePostUseCase {
    @Transactional
    override fun update(command: UpdatePostCommand): UpdatePostResult {
        val post = postPort.findById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        val updated = post.update(
            title = PostTitle.of(command.title),
            content = PostContent.of(command.content),
            requesterUsername = command.requesterUsername
        )
        return UpdatePostResult.of(postPort.save(updated))
    }
}

// DeletePostService.kt
package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.application.post.usecase.DeletePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePostService(private val postPort: PostPort) : DeletePostUseCase {
    @Transactional
    override fun delete(command: DeletePostCommand) {
        val post = postPort.findById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        post.validateOwner(command.requesterUsername)
        postPort.deleteById(command.id)
    }
}
```

- [ ] **Step 7: 테스트 통과 확인**

```bash
./gradlew :oop-application:test --tests "com.jaeyong.oop.application.post.*"
```

Expected: PASS

- [ ] **Step 8: 커밋**

```bash
git add oop-application/src/
git commit -m "feat(application): 게시판 UseCase / Command / Result / Service 구현"
```

---

## Task 5: Infrastructure 계층 구현

**Files:**
- Create: `oop-infrastructure/src/main/kotlin/com/jaeyong/oop/infrastructure/post/entity/PostEntity.kt`
- Create: `oop-infrastructure/src/main/kotlin/com/jaeyong/oop/infrastructure/post/repository/PostEntityRepository.kt`
- Create: `oop-infrastructure/src/main/kotlin/com/jaeyong/oop/infrastructure/post/jpa/PostJpaRepository.kt`
- Create: `oop-infrastructure/src/main/kotlin/com/jaeyong/oop/infrastructure/post/adapter/PostPersistenceAdapter.kt`
- Create: `oop-infrastructure/src/test/kotlin/com/jaeyong/oop/infrastructure/post/entity/PostEntityTest.kt`
- Create: `oop-infrastructure/src/test/kotlin/com/jaeyong/oop/infrastructure/post/adapter/PostPersistenceAdapterTest.kt`

- [ ] **Step 1: PostEntityTest 작성**

```kotlin
package com.jaeyong.oop.infrastructure.post.entity

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PostEntityTest {

    @Test
    fun `fromDomain으로 Post를 PostEntity로 변환한다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        val entity = PostEntity.fromDomain(post)

        assertThat(entity.id).isEqualTo(1L)
        assertThat(entity.title).isEqualTo("제목")
        assertThat(entity.content).isEqualTo("내용")
        assertThat(entity.authorUsername).isEqualTo("user1")
    }

    @Test
    fun `toDomain으로 PostEntity를 Post로 변환한다`() {
        val now = LocalDateTime.now()
        val entity = PostEntity(1L, "제목", "내용", "user1", now, null)
        val post = entity.toDomain()

        assertThat(post.id).isEqualTo(1L)
        assertThat(post.title.value).isEqualTo("제목")
        assertThat(post.content.value).isEqualTo("내용")
        assertThat(post.authorUsername).isEqualTo("user1")
    }
}
```

- [ ] **Step 2: PostPersistenceAdapterTest 작성**

```kotlin
package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PostPersistenceAdapterTest {

    @Mock private lateinit var postEntityRepository: PostEntityRepository
    @InjectMocks private lateinit var sut: PostPersistenceAdapter

    @Test
    fun `save는 저장된 Post를 반환한다`() {
        val post = Post.create(PostTitle.of("제목"), PostContent.of("내용"), "user1")
        val savedEntity = PostEntity(1L, "제목", "내용", "user1", post.createdAt, null)
        given(postEntityRepository.save(any())).willReturn(savedEntity)

        val result = sut.save(post)

        assertThat(result.id).isEqualTo(1L)
    }

    @Test
    fun `findById는 존재하면 Post를 반환한다`() {
        val entity = PostEntity(1L, "제목", "내용", "user1", LocalDateTime.now(), null)
        given(postEntityRepository.findById(1L)).willReturn(entity)

        val result = sut.findById(1L)

        assertThat(result).isNotNull()
        assertThat(result!!.id).isEqualTo(1L)
    }

    @Test
    fun `findById는 존재하지 않으면 null을 반환한다`() {
        given(postEntityRepository.findById(999L)).willReturn(null)

        val result = sut.findById(999L)

        assertThat(result).isNull()
    }
}
```

- [ ] **Step 3: 테스트 실패 확인**

```bash
./gradlew :oop-infrastructure:compileTestKotlin
```

Expected: FAIL (클래스 없음)

- [ ] **Step 4: PostEntityRepository 구현**

```kotlin
package com.jaeyong.oop.infrastructure.post.repository

import com.jaeyong.oop.infrastructure.post.entity.PostEntity

interface PostEntityRepository {
    fun save(entity: PostEntity): PostEntity
    fun findById(id: Long): PostEntity?
    fun findAll(page: Int, size: Int): List<PostEntity>
    fun countAll(): Long
    fun deleteById(id: Long)
}
```

- [ ] **Step 5: PostEntity 구현**

```kotlin
package com.jaeyong.oop.infrastructure.post.entity

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "posts")
class PostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(name = "author_username", nullable = false)
    val authorUsername: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null
) {
    fun toDomain(): Post = Post.restore(
        id = id,
        title = PostTitle.of(title),
        content = PostContent.of(content),
        authorUsername = authorUsername,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(post: Post): PostEntity = PostEntity(
            id = post.id,
            title = post.title.value,
            content = post.content.value,
            authorUsername = post.authorUsername,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )
    }
}
```

- [ ] **Step 6: PostJpaRepository 구현**

```kotlin
package com.jaeyong.oop.infrastructure.post.jpa

import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostJpaRepository : JpaRepository<PostEntity, Long>, PostEntityRepository {
    @Query("SELECT p FROM PostEntity p ORDER BY p.createdAt DESC")
    fun findAllPaged(pageable: org.springframework.data.domain.Pageable): List<PostEntity>

    override fun findById(id: Long): PostEntity? = findById(id as Any).orElse(null)

    override fun findAll(page: Int, size: Int): List<PostEntity> =
        findAllPaged(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))

    override fun countAll(): Long = count()

    override fun deleteById(id: Long) = deleteById(id as Any)
}
```

- [ ] **Step 7: PostPersistenceAdapter 구현**

```kotlin
package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.stereotype.Repository

@Repository
class PostPersistenceAdapter(
    private val postEntityRepository: PostEntityRepository
) : PostPort {
    override fun save(post: Post): Post =
        postEntityRepository.save(PostEntity.fromDomain(post)).toDomain()

    override fun findById(id: Long): Post? =
        postEntityRepository.findById(id)?.toDomain()

    override fun findAll(page: Int, size: Int): List<Post> =
        postEntityRepository.findAll(page, size).map { it.toDomain() }

    override fun countAll(): Long = postEntityRepository.countAll()

    override fun deleteById(id: Long) = postEntityRepository.deleteById(id)
}
```

- [ ] **Step 8: 테스트 통과 확인**

```bash
./gradlew :oop-infrastructure:test --tests "com.jaeyong.oop.infrastructure.post.*"
```

Expected: PASS

- [ ] **Step 9: 커밋**

```bash
git add oop-infrastructure/src/
git commit -m "feat(infrastructure): 게시판 영속성 구현"
```

---

## Task 6: Presentation 계층 구현

**Files:**
- Create: `oop-presentation/src/main/kotlin/com/jaeyong/oop/presentation/post/request/CreatePostRequest.kt`
- Create: `oop-presentation/src/main/kotlin/com/jaeyong/oop/presentation/post/request/UpdatePostRequest.kt`
- Create: `oop-presentation/src/main/kotlin/com/jaeyong/oop/presentation/post/response/PostResponse.kt`
- Create: `oop-presentation/src/main/kotlin/com/jaeyong/oop/presentation/post/response/PostListResponse.kt`
- Create: `oop-presentation/src/main/kotlin/com/jaeyong/oop/presentation/post/api/PostController.kt`
- Create: `oop-presentation/src/test/kotlin/com/jaeyong/oop/presentation/post/api/PostControllerTest.kt`

- [ ] **Step 1: Request / Response DTO 생성**

```kotlin
// CreatePostRequest.kt
package com.jaeyong.oop.presentation.post.request
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
data class CreatePostRequest(
    @field:NotBlank @field:Size(max = 100) val title: String,
    @field:NotBlank val content: String
)

// UpdatePostRequest.kt
package com.jaeyong.oop.presentation.post.request
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
data class UpdatePostRequest(
    @field:NotBlank @field:Size(max = 100) val title: String,
    @field:NotBlank val content: String
)

// PostResponse.kt
package com.jaeyong.oop.presentation.post.response
import com.jaeyong.oop.application.post.result.GetPostResult
import java.time.LocalDateTime
data class PostResponse(
    val id: Long, val title: String, val content: String,
    val authorUsername: String, val createdAt: LocalDateTime, val updatedAt: LocalDateTime?
) {
    companion object {
        fun of(result: GetPostResult) = PostResponse(
            result.id, result.title, result.content,
            result.authorUsername, result.createdAt, result.updatedAt
        )
    }
}

// PostListResponse.kt
package com.jaeyong.oop.presentation.post.response
import com.jaeyong.oop.application.post.result.GetPostListResult
data class PostListResponse(
    val posts: List<PostResponse>, val totalCount: Long, val page: Int, val size: Int
) {
    companion object {
        fun of(result: GetPostListResult) = PostListResponse(
            result.posts.map { PostResponse(it.id, it.title, it.content, it.authorUsername, it.createdAt, it.updatedAt) },
            result.totalCount, result.page, result.size
        )
    }
}
```

- [ ] **Step 2: PostControllerTest 작성**

```kotlin
package com.jaeyong.oop.presentation.post.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.*
import com.jaeyong.oop.presentation.post.request.CreatePostRequest
import com.jaeyong.oop.presentation.post.request.UpdatePostRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(PostController::class)
class PostControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @MockBean private lateinit var createPostUseCase: CreatePostUseCase
    @MockBean private lateinit var getPostUseCase: GetPostUseCase
    @MockBean private lateinit var getPostListUseCase: GetPostListUseCase
    @MockBean private lateinit var updatePostUseCase: UpdatePostUseCase
    @MockBean private lateinit var deletePostUseCase: DeletePostUseCase

    @Test
    fun `POST posts - 게시글 작성 성공 시 201을 반환한다`() {
        val result = CreatePostResult::class.java.getDeclaredConstructor(
            Long::class.java, String::class.java, String::class.java,
            String::class.java, LocalDateTime::class.java
        ).also { it.isAccessible = true }
            .newInstance(1L, "제목", "내용", "user1", LocalDateTime.now())

        given(createPostUseCase.create(any())).willReturn(result)

        mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("username", "user1")
                .content(objectMapper.writeValueAsString(CreatePostRequest("제목", "내용")))
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `GET posts id - 게시글 단건 조회 성공 시 200을 반환한다`() {
        val result = GetPostResult::class.java.getDeclaredConstructor(
            Long::class.java, String::class.java, String::class.java,
            String::class.java, LocalDateTime::class.java, LocalDateTime::class.java
        ).also { it.isAccessible = true }
            .newInstance(1L, "제목", "내용", "user1", LocalDateTime.now(), null)

        given(getPostUseCase.get(any())).willReturn(result)

        mockMvc.perform(get("/posts/1"))
            .andExpect(status().isOk)
    }

    @Test
    fun `DELETE posts id - 게시글 삭제 성공 시 204를 반환한다`() {
        mockMvc.perform(
            delete("/posts/1")
                .requestAttr("username", "user1")
        )
            .andExpect(status().isNoContent)
    }
}
```

- [ ] **Step 3: 테스트 실패 확인**

```bash
./gradlew :oop-presentation:compileTestKotlin
```

Expected: FAIL (PostController 없음)

- [ ] **Step 4: PostController 구현**

```kotlin
package com.jaeyong.oop.presentation.post.api

import com.jaeyong.oop.application.post.command.*
import com.jaeyong.oop.application.post.usecase.*
import com.jaeyong.oop.presentation.auth.CurrentUser
import com.jaeyong.oop.presentation.post.request.CreatePostRequest
import com.jaeyong.oop.presentation.post.request.UpdatePostRequest
import com.jaeyong.oop.presentation.post.response.PostListResponse
import com.jaeyong.oop.presentation.post.response.PostResponse
import com.jaeyong.oop.presentation.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/posts")
class PostController(
    private val createPostUseCase: CreatePostUseCase,
    private val getPostUseCase: GetPostUseCase,
    private val getPostListUseCase: GetPostListUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreatePostRequest,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        createPostUseCase.create(CreatePostCommand.of(request.title, request.content, username))
        return ApiResponse.success(HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ApiResponse<PostResponse>> {
        val result = getPostUseCase.get(GetPostCommand.of(id))
        return ApiResponse.success(PostResponse.of(result), HttpStatus.OK)
    }

    @GetMapping
    fun getList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PostListResponse>> {
        val result = getPostListUseCase.getList(GetPostListCommand.of(page, size))
        return ApiResponse.success(PostListResponse.of(result), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePostRequest,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        updatePostUseCase.update(UpdatePostCommand.of(id, request.title, request.content, username))
        return ApiResponse.success(HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        deletePostUseCase.delete(DeletePostCommand.of(id, username))
        return ApiResponse.success(HttpStatus.NO_CONTENT)
    }
}
```

- [ ] **Step 5: 테스트 통과 확인**

```bash
./gradlew :oop-presentation:test --tests "com.jaeyong.oop.presentation.post.*"
```

Expected: PASS

- [ ] **Step 6: 전체 빌드 확인**

```bash
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 7: 커밋**

```bash
git add oop-presentation/src/
git commit -m "feat(presentation): PostController 구현"
```

---

## Task 7: 설계 문서 커밋

- [ ] **Step 1: 설계 문서 커밋**

```bash
git add docs/board/BOARD.md docs/superpowers/plans/2026-04-15-board-implementation.md
git commit -m "feat: 게시판 설계 문서 및 구현 플랜 추가"
```
