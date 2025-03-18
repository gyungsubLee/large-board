package sub.board.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sub.board.comment.entity.Comment;

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
}