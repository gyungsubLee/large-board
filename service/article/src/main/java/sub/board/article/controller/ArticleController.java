package sub.board.article.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sub.board.article.service.ArticleService;
import sub.board.article.service.request.ArticleCreateRequest;
import sub.board.article.service.request.ArticleUpdateRequest;
import sub.board.article.service.response.ArticleResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public ArticleResponse create(@RequestBody ArticleCreateRequest request) {
        return articleService.create(request);
    }

    @GetMapping("/{articleId}")
    public ArticleResponse read(@PathVariable Long articleId) {
        return articleService.read(articleId);
    }

    @PatchMapping("/{articleId}")
    public ArticleResponse update(@PathVariable Long articleId, @RequestBody ArticleUpdateRequest request) {
        return articleService.update(articleId, request);
    }

    @DeleteMapping("/{articleId}")
    public void delete(@PathVariable Long articleId) {
        articleService.delete(articleId);
    }
}
