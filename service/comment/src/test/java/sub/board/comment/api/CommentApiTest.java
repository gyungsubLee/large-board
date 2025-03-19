package sub.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import sub.board.comment.service.response.CommentResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");
    String baseUrl = "/api/v1/comments";

    @DisplayName("[댓글 생성] Depth: 2")
    @Test
    void createTest() {
        // Given
        Long articleId = 1L;
        Long writerId = 1L;
        String content1 = "테스트1";
        String content2 = "테스트2";

        // When
          // Parent Comment
        CommentCreateRequest request1 = new CommentCreateRequest(articleId, content1, null, writerId);
        CommentResponse parentCmtResponse = createComment(request1);

          // Child Comment
        Long parentCommentId = parentCmtResponse.getCommentId();
        CommentCreateRequest request2 = new CommentCreateRequest(articleId, content2, parentCommentId, writerId);
        CommentResponse childCmtResponse = createComment(request2);

        // Then
          // Parent Comment
        assertNotNull(parentCmtResponse);
        assertThat(parentCmtResponse.getContent()).isEqualTo(content1);
        assertThat(parentCmtResponse.getParentCommentId()).isEqualTo(parentCmtResponse.getCommentId());  // 최상위 댓글인 경우, commentId 와 parentCommentId가 동일하다.

          // Child Comment
        assertNotNull(childCmtResponse);
        assertThat(childCmtResponse.getContent()).isEqualTo(content2);
        assertThat(childCmtResponse.getParentCommentId()).isEqualTo(parentCommentId);
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri(baseUrl)
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Getter
    @AllArgsConstructor
    private class CommentCreateRequest {
        private Long articleId;
        private String cotent;
        private Long parentCommentId;
        private Long writerId;
    }
}
