package sub.board.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import sub.board.article.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    default Article findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() ->
                // TODO: Exception 헨들링
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));
    }
}