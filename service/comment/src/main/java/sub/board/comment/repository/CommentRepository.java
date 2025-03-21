package sub.board.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sub.board.comment.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    default Comment findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당하는 댓글이 존재하지 않습니다. "));
    }

    @Query(
            value = """
                    SELECT COUNT(*) FROM (
                        SELECT comment_id FROM comment
                        WHERE article_id = :articleId AND parent_comment_id = :parentCommentId
                        LIMIT :limit
                    ) t
            """,
            nativeQuery = true)
    Long countBy(
            @Param("articleId") Long articleId,
            @Param("parentCommentId") Long parentCommentId,
            @Param("limit") Long limit
    );

    @Query(
            value = """
                    SELECT c.comment_id, c.content, c.parent_comment_id, c.article_id, c.writer_id, c.deleted, c.created_at
                    FROM (
                        SELECT comment_id
                        FROM comment
                        WHERE article_id = :articleId
                        ORDER BY parent_comment_id ASC, comment_id ASC
                        LIMIT :limit OFFSET :offset
                    ) t
                    LEFT JOIN comment c ON t.comment_id = c.comment_id
            """,
            nativeQuery = true)
    List<Comment> findAll(
            @Param("articleId") Long articleId,
            @Param("limit") Long limit,
            @Param("offset") Long offset
    );

    @Query(
            value = """
                    SELECT count(*)
                    FROM (
                        SELECT comment_id
                        FROM comment
                        WHERE article_id = :articleId
                        Limit :limit
                    ) t
            """,
            nativeQuery = true)
    Long count(
        @Param("articleId") Long articleId,
        @Param("limit") Long limit
    );


    @Query(
            value = """
                    SELECT c.comment_id, c.content, c.parent_comment_id, c.article_id, c.writer_id, c.deleted, c.created_at
                    FROM comment c
                    WHERE article_id = :articleId
                    ORDER BY parent_comment_id ASC, comment_id ASC
                    Limit :limit
            """,
            nativeQuery = true)
    List<Comment> findAllInfiniteScroll(
            @Param("articleId") Long articleId,
            @Param("limit") Long limit
    );

    @Query(
            value = """
                    SELECT c.comment_id, c.content, c.parent_comment_id, c.article_id, c.writer_id, c.deleted, c.created_at
                    FROM comment c
                    WHERE article_id = :articleId AND (
                        parent_comment_id > :lastParentCommentId OR
                        (parent_comment_id = :lastParentCommentId AND comment_id > :lastCommentId)
                    )
                    ORDER BY parent_comment_id ASC, comment_id ASC
                    Limit :limit
            """,
            nativeQuery = true)
    List<Comment> findAllInfiniteScroll(
            @Param("articleId") Long articleId,
            @Param("lastParentCommentId") Long lastParentCommentId,
            @Param("lastCommentId") Long lastCommentId,
            @Param("limit") Long limit
    );
}