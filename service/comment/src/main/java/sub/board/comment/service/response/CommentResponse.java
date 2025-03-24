package sub.board.comment.service.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sub.board.comment.entity.Comment;
import sub.board.comment.entity.CommentV2;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {
    private Long commentId;
    private String content;
    private Long parentCommentId;
    private Long articleId;
    private Long writerId;
    private String path;
    private Boolean deleted;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.commentId = comment.getCommentId();
        response.content = comment.getContent();
        response.parentCommentId = comment.getParentCommentId();
        response.articleId = comment.getArticleId();
        response.writerId = comment.getWriterId();
        response.deleted = comment.getDeleted();
        response.createdAt = comment.getCreatedAt();
        return response;
    }

    public static CommentResponse from(CommentV2 comment) {
        CommentResponse response = new CommentResponse();
        response.commentId = comment.getCommentId();
        response.content = comment.getContent();
        response.path = comment.getCommentPath().getPath();
        response.articleId = comment.getArticleId();
        response.writerId = comment.getWriterId();
        response.deleted = comment.getDeleted();
        response.createdAt = comment.getCreatedAt();
        return response;
    }
}