# Board — 게시판

> **Spring Security 미사용** · JWT 인증 연동 · Hexagonal Architecture

---

## 설계 원칙

- 게시글 작성·수정·삭제는 JWT 인증 필요, 조회는 비인증 허용
- 수정·삭제는 작성자 본인만 가능 (서비스 레이어에서 username 비교)
- 도메인 객체(`Post`)가 소유권 검증 책임을 직접 가짐 (TDA 원칙)

---

## 컴포넌트 배치

```mermaid
flowchart LR
    subgraph presentation
        PC["PostController"]
    end

    subgraph application
        subgraph usecase["UseCase"]
            CPU["CreatePostUseCase"]
            GPU["GetPostUseCase"]
            GPLU["GetPostListUseCase"]
            UPU["UpdatePostUseCase"]
            DPU["DeletePostUseCase"]
        end

        subgraph service["Service"]
            CPS["CreatePostService"]
            GPS["GetPostService"]
            GPLS["GetPostListService"]
            UPS["UpdatePostService"]
            DPS["DeletePostService"]
        end
    end

    subgraph domain["domain — Port"]
        PP["PostPort"]
    end

    subgraph infrastructure["infrastructure — Adapter"]
        PPA["PostPersistenceAdapter"]
        PER["PostEntityRepository"]
        PJR["PostJpaRepository\n(JpaRepository)"]
    end

    PC --CreatePostCommand--> CPU
    PC --GetPostCommand--> GPU
    PC --GetPostListCommand--> GPLU
    PC --UpdatePostCommand--> UPU
    PC --DeletePostCommand--> DPU

    CPU -. implements .-> CPS
    GPU -. implements .-> GPS
    GPLU -. implements .-> GPLS
    UPU -. implements .-> UPS
    DPU -. implements .-> DPS

    CPS & GPS & GPLS & UPS & DPS --> PP

    PPA -. implements .-> PP
    PPA --> PER
    PJR -. implements .-> PER
```

---

## 도메인 설계

```mermaid
classDiagram
    class Post {
        -Long? id
        -PostTitle title
        -PostContent content
        -String authorUsername
        -LocalDateTime createdAt
        -LocalDateTime? updatedAt
        +update(title PostTitle, content PostContent, requesterUsername String)
        +validateOwner(requesterUsername String)
        +create(title PostTitle, content PostContent, authorUsername String)$ Post
        +restore(...)$ Post
    }

    class PostTitle {
        -String value
        +MAX_LENGTH 100
        +of(value String)$ PostTitle
    }

    class PostContent {
        -String value
        +MAX_LENGTH 5000
        +of(value String)$ PostContent
    }

    Post --> PostTitle : title
    Post --> PostContent : content
```


### 비즈니스 규칙

| 구분 | 대상 | 규칙 |
|---|---|---|
| VO | `PostTitle` | 공백 불가 · 최대 100자 |
| VO | `PostContent` | 공백 불가 · 최대 5000자 |
| Entity | `Post.create()` | 새 게시글 — `id` null, `createdAt` 현재시각 자동 |
| Entity | `Post.restore()` | DB 복원 — 규칙 검증 없이 순수 복원 |
| Entity | `Post.update()` | 소유권 검증 → title·content·updatedAt 갱신 |
| Entity | `Post.validateOwner()` | 작성자 불일치 시 `UNAUTHORIZED` |

---

## 게시글 작성 (POST /posts)

```mermaid
sequenceDiagram
    actor Client
    participant C  as PostController
    participant S  as CreatePostService
    participant R  as PostRepository
    participant DB as PostgreSQL

    Client ->>+ C: POST /posts<br/>{title, content}<br/>Authorization: Bearer {token}
    C ->>+ S: create(CreatePostCommand)
    S ->>+ R: save(Post)
    R ->>+ DB: INSERT posts
    DB -->>- R: saved
    R -->>- S: Post
    S -->>- C: CreatePostResult
    C -->>- Client: 201 Created<br/>{id, title, content, author, createdAt}
```

---

## 게시글 단건 조회 (GET /posts/{id})

```mermaid
sequenceDiagram
    actor Client
    participant C  as PostController
    participant S  as GetPostService
    participant R  as PostRepository
    participant DB as PostgreSQL

    Client ->>+ C: GET /posts/{id}
    C ->>+ S: get(GetPostCommand)
    S ->>+ R: findById(id)
    R ->>+ DB: SELECT posts WHERE id=?
    DB -->>- R: PostEntity
    R -->>- S: Post?
    alt 없음
        S -->> C: BaseException(NOT_FOUND)
        C -->> Client: 404 Not Found
    end
    S -->>- C: GetPostResult
    C -->>- Client: 200 OK<br/>{id, title, content, author, createdAt, updatedAt}
```

---

## 게시글 목록 (GET /posts)

```mermaid
sequenceDiagram
    actor Client
    participant C  as PostController
    participant S  as GetPostListService
    participant R  as PostRepository
    participant DB as PostgreSQL

    Client ->>+ C: GET /posts?page=0&size=20
    C ->>+ S: getList(GetPostListCommand)
    S ->>+ R: findAll(page, size)
    R ->>+ DB: SELECT posts ORDER BY createdAt DESC LIMIT ? OFFSET ?
    DB -->>- R: List<PostEntity>
    R -->>- S: List<Post>
    S -->>- C: GetPostListResult
    C -->>- Client: 200 OK<br/>{posts: [...], totalCount, page, size}
```

---

## 게시글 수정 (PUT /posts/{id})

```mermaid
sequenceDiagram
    actor Client
    participant C  as PostController
    participant S  as UpdatePostService
    participant R  as PostRepository
    participant DB as PostgreSQL

    Client ->>+ C: PUT /posts/{id}<br/>{title, content}<br/>Authorization: Bearer {token}
    C ->>+ S: update(UpdatePostCommand)
    S ->>+ R: findById(id)
    R ->>+ DB: SELECT posts WHERE id=?
    DB -->>- R: PostEntity
    R -->>- S: Post?
    alt 없음
        S -->> C: BaseException(NOT_FOUND)
        C -->> Client: 404 Not Found
    end
    S ->> S: post.update(title, content, requesterUsername)
    alt 작성자 불일치
        S -->> C: BaseException(UNAUTHORIZED)
        C -->> Client: 401 Unauthorized
    end
    S ->>+ R: save(updatedPost)
    R ->>+ DB: UPDATE posts SET ...
    DB -->>- R: saved
    R -->>- S: Post
    S -->>- C: UpdatePostResult
    C -->>- Client: 200 OK<br/>{id, title, content, author, updatedAt}
```

---

## 게시글 삭제 (DELETE /posts/{id})

```mermaid
sequenceDiagram
    actor Client
    participant C  as PostController
    participant S  as DeletePostService
    participant R  as PostRepository
    participant DB as PostgreSQL

    Client ->>+ C: DELETE /posts/{id}<br/>Authorization: Bearer {token}
    C ->>+ S: delete(DeletePostCommand)
    S ->>+ R: findById(id)
    R ->>+ DB: SELECT posts WHERE id=?
    DB -->>- R: PostEntity
    R -->>- S: Post?
    alt 없음
        S -->> C: BaseException(NOT_FOUND)
        C -->> Client: 404 Not Found
    end
    S ->> S: post.validateOwner(requesterUsername)
    alt 작성자 불일치
        S -->> C: BaseException(UNAUTHORIZED)
        C -->> Client: 401 Unauthorized
    end
    S ->>+ R: deleteById(id)
    R ->>+ DB: DELETE posts WHERE id=?
    DB -->>- R: ok
    R -->>- S: void
    S -->>- C: void
    C -->>- Client: 204 No Content
```

---

## PostEntity (JPA 매핑)

```mermaid
classDiagram
    class Post {
        -Long? id
        -PostTitle title
        -PostContent content
        -String authorUsername
        -LocalDateTime createdAt
        -LocalDateTime? updatedAt
    }

    class PostEntity {
        -Long? id
        -String title
        -String content
        -String authorUsername
        -LocalDateTime createdAt
        -LocalDateTime? updatedAt
        +toDomain() Post
        +fromDomain(post Post)$ PostEntity
    }

    class DB {
        +BIGINT id PK
        +VARCHAR title
        +TEXT content
        +VARCHAR authorUsername
        +TIMESTAMP createdAt
        +TIMESTAMP updatedAt
    }

    PostEntity ..> Post : toDomain()
    Post ..> PostEntity : fromDomain()
    PostEntity --> DB : JPA 매핑
```

---

## 에러코드

| 코드 | 상수 | 발생 상황 | 신규 여부 |
|---|---|---|---|
| `D001` | `NOT_FOUND` | 게시글 없음 | 기존 재사용 |
| `D005` | `POST_ACCESS_DENIED` | 본인 게시글 아님 (수정·삭제) | 신규 |

> `D005`를 `A001`로 퉁치지 않고 분리한 이유 — "로그인 안 됨"과 "본인 글 아님"은 클라이언트 입장에서 다른 상황이라 구분하는 게 명확함

---

## API

| Method | Path | 설명 | Body | 인증 | 성공 | 실패 |
|---|---|---|---|---|---|---|
| `POST` | `/posts` | 게시글 작성 | `{title, content}` | 필요 | `201` | `401` |
| `GET` | `/posts` | 게시글 목록 조회 (페이징) | - | 불필요 | `200` | - |
| `GET` | `/posts/{id}` | 게시글 단건 조회 | - | 불필요 | `200` | `404` D001 |
| `PUT` | `/posts/{id}` | 게시글 수정 (본인만) | `{title, content}` | 필요 | `200` | `404` D001 · `403` D005 |
| `DELETE` | `/posts/{id}` | 게시글 삭제 (본인만) | - | 필요 | `204` | `404` D001 · `403` D005 |
