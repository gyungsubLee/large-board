package sub.board.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sub.board.comment.entity.Comment;
import sub.board.comment.repository.CommentRepository;
import sub.board.comment.service.request.CommentCreateRequest;
import sub.board.comment.service.response.CommentPageResponse;
import sub.board.comment.service.response.CommentResponse;
import sub.board.common.snowflake.Snowflake;

import java.util.List;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final Snowflake snowflake = new Snowflake();
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse create(CommentCreateRequest request) {
        Comment parent = findParent(request.getParentCommentId());

        Comment buildComment = Comment.builder()
                .commentId(snowflake.nextId())
                .content(request.getCotent())
                .parentCommentId(parent == null ? null : parent.getCommentId())
                .articleId(request.getArticleId())
                .writerId(request.getWriterId())
                .build();

        Comment comment = commentRepository.save(buildComment);

        return CommentResponse.from(comment);
    }

    private Comment findParent(Long parentCommentId) {
        if (parentCommentId == null) {
            return null;
        }
        return commentRepository.findById(parentCommentId)
                .filter(not(Comment::getDeleted))
                .filter(Comment::isRoot)
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public CommentResponse read(Long commentId) {
        return CommentResponse.from(
                commentRepository.findByIdOrElseThrow(commentId)
        );
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
                .filter(not(Comment::getDeleted))
                .ifPresent(comment -> {
                    if (hasChildren(comment)) {
                        comment.delete();
                    } else {
                        delete(comment);
                    }
                });
    }

    private boolean hasChildren(Comment comment) {
        // 하위 댓글 한개만 존재해도 limit 2로 조회한 댓글 개수는 2이다.
        return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
    }

    private void delete(Comment comment) {
        commentRepository.delete(comment);
        if (!comment.isRoot()) {
            commentRepository.findById(comment.getParentCommentId())
                    .filter(Comment::getDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::delete);
        }
    }

    public CommentPageResponse readAll(Long articleId, Long page, Long pageSize) {
        return CommentPageResponse.of(
                commentRepository.findAll(articleId, pageSize, (page - 1) * pageSize).stream()
                        .map(CommentResponse::from)
                        .toList(),
                commentRepository.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        );
    }

    public List<CommentResponse> readAllInfiniteScroll(Long articleId, Long limit, Long lastParentCommentId, Long lastCommentId) {
        List<Comment> comments = lastParentCommentId == null || lastCommentId == null ?
                commentRepository.findAllInfiniteScroll(articleId, limit) :
                commentRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit);
        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }
}