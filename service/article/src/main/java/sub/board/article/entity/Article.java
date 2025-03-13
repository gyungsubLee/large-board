package sub.board.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "article")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id
    private Long articleId;
    private String title;
    private String content;
    private Long boardId; // Shard Key
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public Article(Long articleId, String title, String content, Long boardId, Long writerId) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.boardId = boardId;
        this.writerId = writerId;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = this.createdAt;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        modifiedAt = LocalDateTime.now();
    }
}