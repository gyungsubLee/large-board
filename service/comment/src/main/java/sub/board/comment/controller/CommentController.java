package sub.board.comment.controller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sub.board.comment.service.CommentService;
import sub.board.comment.service.request.CommentCreateRequest;
import sub.board.comment.service.response.CommentPageResponse;
import sub.board.comment.service.response.CommentResponse;

import java.util.List;

// TODO: 응답 포맷 통일 및 ResponseEntity 설정
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentResponse create(@RequestBody CommentCreateRequest request) {
        return commentService.create(request);
    }

    @GetMapping("/{commentId}")
    public CommentResponse read(@PathVariable Long commentId) {
        return commentService.read(commentId);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok().body("정삭적으로 삭제처리 되었습니다.");
    }

    @GetMapping
    public CommentPageResponse readAll(
            @RequestParam Long articleId,
            @RequestParam Long page,
            @RequestParam Long pageSize) {
        return commentService.readAll(articleId, page, pageSize);
    }

    @GetMapping("/infinite-scroll")
    public List<CommentResponse> readAllInfiniteScroll(
            @RequestParam Long articleId,
            @RequestParam Long limit,
            @RequestParam @Nullable Long lastParentCommentId,
            @RequestParam @Nullable Long lastCommentId) {
        return commentService.readAllInfiniteScroll(articleId, limit, lastParentCommentId, lastCommentId);
    }
}