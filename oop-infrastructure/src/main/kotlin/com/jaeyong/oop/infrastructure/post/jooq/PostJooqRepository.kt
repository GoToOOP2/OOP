package com.jaeyong.oop.infrastructure.post.jooq

import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PostJooqRepository(
    private val dsl: DSLContext
) {
    fun findPageWithTotal(page: Int, size: Int, direction: String): Pair<List<PostEntity>, Long> {
        val postsTable = table("posts")
        val idField = field("id", Long::class.java)
        val titleField = field("title", String::class.java)
        val contentField = field("content", String::class.java)
        val authorField = field("author_username", String::class.java)
        val createdAtField = field("created_at", LocalDateTime::class.java)
        val updatedAtField = field("updated_at", LocalDateTime::class.java)

        val totalField = count().over().`as`("total_count")
        val orderBy = if (direction.equals("ASC", ignoreCase = true)) idField.asc() else idField.desc()

        val records = dsl.select(asterisk(), totalField)
            .from(postsTable)
            .orderBy(orderBy)
            .limit(size)
            .offset(page * size)
            .fetch()

        val total = if (records.isEmpty()) 0L else records[0].get(totalField).toLong()

        val posts = records.map { record ->
            PostEntity(
                id = record.get(idField),
                title = record.get(titleField),
                content = record.get(contentField),
                authorUsername = record.get(authorField),
                createdAt = record.get(createdAtField),
                updatedAt = record.get(updatedAtField)
            )
        }

        return posts to total
    }
}
