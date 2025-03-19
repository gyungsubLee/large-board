package sub.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import sub.board.comment.service.response.CommentResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");
    String baseUrl = "/api/v1/comments";

    String parentContent = "부모 댓글 content";
    String childContent = "자식 댓글 content";
    Long parentCommentId;

    CommentResponse parentCmtResponse;
    CommentResponse childCmtResponse;


    @DisplayName("댓글 생성(depth: 2) 기본 설정")
    @BeforeEach
    void setUp() {
        // Given
        Long articleId = 1L;
        Long writerId = 1L;

        // When
        // Parent Comment
        CommentCreateRequest request1 = new CommentCreateRequest(articleId, parentContent, null, writerId);
        parentCmtResponse = createComment(request1);

        // Child Comment
        parentCommentId = parentCmtResponse.getCommentId();
        CommentCreateRequest request2 = new CommentCreateRequest(articleId, childContent, parentCommentId, writerId);
        childCmtResponse = createComment(request2);
    }

    @DisplayName("[댓글 생성] Depth: 2")
    @Test
    void createTest() {
        // Then
          // Parent Comment
        assertNotNull(parentCmtResponse);
        assertThat(parentCmtResponse.getContent()).isEqualTo(parentContent);
        assertThat(parentCmtResponse.getParentCommentId()).isEqualTo(parentCmtResponse.getCommentId());  // 최상위 댓글인 경우, commentId 와 parentCommentId가 동일하다.

          // Child Comment
        assertNotNull(childCmtResponse);
        assertThat(childCmtResponse.getContent()).isEqualTo(childContent);
        assertThat(childCmtResponse.getParentCommentId()).isEqualTo(parentCommentId);
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri(baseUrl)
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }
    
    @DisplayName("[댓글 조회] Depth: 2")
    @Test
    void readTest() {
        // Given
        Long parentCmtId = parentCmtResponse.getCommentId();
        Long childCmdId = childCmtResponse.getCommentId();

        // When
        CommentResponse parentCmtRes = readComment(parentCmtId);
        CommentResponse childCmtRes = readComment(childCmdId);

        // Then
        assertNotNull(parentCmtRes);
        assertThat(parentCmtRes.getCommentId()).isEqualTo(parentCmtId);

        assertNotNull(childCmtRes);
        assertThat(childCmtRes.getCommentId()).isEqualTo(childCmdId);
    }

    CommentResponse readComment(Long commentId) {
        return restClient.get()
                .uri(baseUrl + "/{commentId}", commentId)
                .retrieve()
                .body(CommentResponse.class);
    }

    @DisplayName("[댓글 삭제1] 하위 댓글이 존재하는 경우, mark( 'deleted=true' ) 표시만 한다.")
    @Test
    void parentDeleteTest() {
        // Given
        Long parentCmtId = parentCmtResponse.getCommentId();

        // When
        deleteComment(parentCommentId);
        CommentResponse parentCmtRes = readComment(parentCommentId);

        // Then
        assertThat(parentCmtRes.getDeleted()).isTrue();
    }


    @DisplayName("[댓글 삭제2] 하위 댓글이 존재하지 않는 경우, 해당 댓글을 바로 삭제한다. ")
    @Test
    void childDeleteTest() {
        // Given
        Long childCmdId = childCmtResponse.getCommentId();

        // When
        deleteComment(childCmdId);

        // Then
        // Notes: 서버 내부에서는 IllegalArgumentException 이 발생되지만, RestClient릍 통한 외부에서 api 호출이기 때문에 결과적으로는 500 에러(HttpServerErrorException)가 반환된다.
        assertThrows(HttpStatusCodeException.class, () -> readComment(childCmdId));
    }

    @DisplayName("[댓글 삭제3] 부모 댓글이 mark('deleted=true')된 상태에서 하위 댓글이 모두 삭제되면 같이 삭제 처리된다.")
    @Test
    void AllDeleteTest() {
        // Given
        Long parentCmtId = parentCmtResponse.getCommentId();
        Long childCmdId = childCmtResponse.getCommentId();

        // When
        deleteComment(parentCmtId);
        deleteComment(childCmdId);

        // Then
        assertThrows(HttpStatusCodeException.class, () -> readComment(childCmdId));
        assertThrows(HttpStatusCodeException.class, () -> readComment(parentCmtId));
    }


    void deleteComment(Long commentId) {
        restClient.delete()
                .uri(baseUrl + "/{commentId}", commentId)
                .retrieve();
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
