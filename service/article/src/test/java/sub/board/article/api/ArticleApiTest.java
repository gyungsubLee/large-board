package sub.board.article.api;


import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.UriComponentsBuilder;
import sub.board.article.service.response.ArticlePageResponse;
import sub.board.article.service.response.ArticleResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    // 생성 test 에서 출력된 id 값 사용
    Long articleId;

    // Article id 동적 생성
    @DisplayName("Article 생성")
    @BeforeEach
    void setUp() {
        //
        ArticleCreateRequest request = new ArticleCreateRequest("hi", "Ctest", 1L, 1L);

        // 테스트용 게시글을 먼저 생성하고 ID를 저장
        ArticleResponse response = create(request);
        assertNotNull(response, "생성된 게시글이 null이면 안 됩니다.");

        this.articleId = response.getArticleId();  // 동적으로 생성된 ID 저장
        System.out.println("생성된 articleId = " + this.articleId);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/api/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @DisplayName("게시글 조회 - 무한스크롤")
    @Test
    void readAllInfiniteScrollTest() {
        // Given
        Long boardId = 1L;
        Long pageSize = 30L;
        Long lastArticleId;

        // When
        List<ArticleResponse> articles = readAllInfiniteScroll(boardId, pageSize, null);

        lastArticleId = articles.getLast().getArticleId();

        List<ArticleResponse> articles2 = readAllInfiniteScroll(boardId, pageSize, lastArticleId);

        // Then
        System.out.println("First Page!!");
        for (ArticleResponse response : articles) {
            System.out.println("response1 = " + response.getArticleId());
        }

        System.out.println("Second Page!!");
        for (ArticleResponse response : articles2) {
            System.out.println("response2 = " + response.getArticleId());
        }
    }

    List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, @Nullable Long lastArticleId) {
        String uri = UriComponentsBuilder.fromPath("/api/v1/articles/infinite-scroll")
                .queryParam("boardId", boardId)
                .queryParam("pageSize", pageSize)
                .queryParam("lastArticleId", lastArticleId)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});
    }



    @DisplayName("전체 게시글 페이지 조회")
    @Test
    void readAllTest() {
        // Given
        Long boardId = 1L;
        Long page = 1L;
        Long pageSize =100L;

        // When
        ArticlePageResponse response = readAll(boardId, page, pageSize);

        // Then
        assertNotNull(response);

        System.out.println("response.getArticleCount() = " + response.getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
        String uri = UriComponentsBuilder.fromPath("/api/v1/articles")
                .queryParam("boardId", boardId)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(ArticlePageResponse.class);
    }

    @DisplayName("Article 조회")
    @Test
    void readTest() {
        // When
        ArticleResponse response = read(articleId);

        // Then
        assertNotNull(response, "response 객체가 null 이면 안됩니다.");
        assertThat(response.getArticleId()).isEqualTo(articleId);

        System.out.println("read = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/api/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @DisplayName("Article 수정")
    @Test
    void updateTest() {
        // Given
        ArticleUpdateRequest request = new ArticleUpdateRequest("h12", "Ctest2");

        // When
        ArticleResponse response = update(articleId, request);

        // Then
        assertNotNull(response, "response 객체는 null 이면 안됩니다.");
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getContent()).isEqualTo(request.getContent());

        System.out.println("response = " + response);
    }

    ArticleResponse update(Long articleId, ArticleUpdateRequest requset) {
        return restClient.patch()
                .uri("/api/v1/articles/{articleId}", articleId)
                .body(requset)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @DisplayName("Article 삭제")
    @Test
    void deleteTest() {
        // When
        restClient.delete()
                .uri("/api/v1/articles/{articleId}", articleId)
                .retrieve();

        // Then
        Exception exception = assertThrows(Exception.class, () -> read(articleId));
        System.out.println("삭제 후 조회 시도 예외: " + exception.getMessage());
    }


    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long boardId;
        private Long writerId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}
