package sub.board.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import sub.board.article.entity.Article;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    default Article findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() ->
                // TODO: Exception 헨들링
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));
    }

    @Query(
        value = """
                SELECT a.article_id, a.title, a.content, a.board_id, a.writer_id, a.created_at, a.modified_at
                FROM (
                    SELECT article_id
                    FROM article
                    WHERE board_id = :board_id
                    ORDER BY article_id DESC
                    LIMIT :limit OFFSET :offset
                ) t
                LEFT JOIN article a ON t.article_id = a.article_id
            """,
        nativeQuery = true)
    List<Article> findAllArticle(
            @Param("board_id") Long boardId,
            @Param("limit") Long limit,
            @Param("offset") Long offset
    );

    @Query(
        value = """
                SELECT COUNT(*) FROM (
                    SELECT article_id FROM article WHERE board_id = :boardId LIMIT :limit
                ) t
            """,
        nativeQuery = true
    )
    Long count(@Param("boardId") Long boardId, @Param("limit") Long limit);
}