package sub.board.comment.service.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentCreateRequest {
    private Long articleId;
    private String cotent;
    private Long parentCommentId;
    private Long writerId;
}