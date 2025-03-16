package sub.board.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sub.board.article.entity.Article;
import sub.board.article.repository.ArticleRepository;
import sub.board.article.service.request.ArticleCreateRequest;
import sub.board.article.service.request.ArticleUpdateRequest;
import sub.board.article.service.response.ArticlePageResponse;
import sub.board.article.service.response.ArticleResponse;
import sub.board.common.snowflake.Snowflake;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article buildArticle = Article.builder()
                .articleId(snowflake.nextId())
                .title(request.getTitle())
                .content(request.getContent())
                .boardId(request.getBoardId())
                .writerId(request.getWriterId())
                .build();

        Article article = articleRepository.save(buildArticle);

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article findArticle = articleRepository.findByIdOrElseThrow(articleId);
        findArticle.update(request.getTitle(), request.getContent());
        return ArticleResponse.from(findArticle);
    }

    @Transactional(readOnly = true)
    public ArticleResponse read(Long articleId) {
        return ArticleResponse.from(articleRepository.findByIdOrElseThrow(articleId));
    }

    @Transactional
    public void delete(Long articleId) {
        articleRepository.deleteById(articleId);
    }

    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
        return ArticlePageResponse.of(
            articleRepository.findAllArticle(boardId, pageSize, (page - 1) * pageSize).stream()
                    .map(ArticleResponse::from)
                    .toList(),
            articleRepository.count(
                    boardId,
                    PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
            )
        );
    }
}