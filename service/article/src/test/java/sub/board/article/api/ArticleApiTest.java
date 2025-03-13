package sub.board.article.api;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import sub.board.article.service.response.ArticleResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    // 생성 test 에서 출력된 id 값 사용
    Long articleId = 158468578773970944L;


    @DisplayName("Article 생성")
    @Test
    void createTest() {
        // Given
        ArticleCreateRequest request = new ArticleCreateRequest("hi", "Ctest", 1L, 1L);

        // When
        ArticleResponse response = create(request);

        // Then
        assertNotNull(response, "response 객체가 null 이면 안됩니다.");
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getContent()).isEqualTo(request.getContent());

        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/api/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
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
