package sub.board.comment.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import sub.board.comment.service.request.CommentCreateRequestV2;
import sub.board.comment.service.response.CommentResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentApiV2Test {
    private final RestClient restClient = RestClient.create("http://localhost:9001");
    private final String baseUrl =  "/api/v2/comments";
    
    @DisplayName("[댓글 생성] Depth: 5")
    @Test
    void createCommentTest() {
        // Given, When
        CommentResponse response1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        CommentResponse response2 = create(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
        CommentResponse response3 = create(new CommentCreateRequestV2(1L, "my comment1", response2.getPath(), 1L));

        System.out.println("response1.getPath() = " + response1.getPath());
        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("\tresponse2.getPath() = " + response2.getPath());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getPath() = " + response3.getPath());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());

        // Then
//        assertThat(response1.getPath()).isEqualTo("00000");
//        assertThat(response2.getPath()).isEqualTo("0000000000");
//        assertThat(response3.getPath()).isEqualTo("000000000000000");

    }

    CommentResponse create(CommentCreateRequestV2 request) {
        return restClient.post()
                .uri(baseUrl)
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }



}
