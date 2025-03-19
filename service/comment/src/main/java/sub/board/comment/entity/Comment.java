package sub.board.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "comment")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    private Long commentId;
    private String content;
    private Long parentCommentId;
    private Long articleId;
    private Long writerId;
    private Boolean deleted;
    private LocalDateTime createdAt;

    @Builder
    public Comment(Long commentId, String content, Long parentCommentId, Long articleId, Long writerId) {
        this.commentId = commentId;
        this.content = content;
        this.parentCommentId = parentCommentId == null? commentId : parentCommentId;
        this.articleId = articleId;
        this.writerId = writerId;
        this.deleted = false;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isRoot() {
        return parentCommentId.longValue() == commentId;
    }

    public void delete() {
        this.deleted = true;
    }
}
