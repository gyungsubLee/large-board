package sub.board.article.api;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import sub.board.article.service.response.ArticleResponse;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    // 생성 test 에서 출력된 id 값 사용
    Long articleId = 158468578773970944L;


    @DisplayName("Article 생성")
    @Test
    void createTest() {
        ArticleResponse response = create(
                new ArticleCreateRequest("hi", "Ctest", 1L, 1L)
        );

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

        ArticleResponse response = read(articleId);
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
        ArticleResponse respose = update(articleId, new ArticleUpdateRequest("h12", "Ctest2"));
        System.out.println("respose = " + respose);
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
        restClient.delete()
                .uri("/api/v1/articles/{articleId}", articleId)
                .retrieve();
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
