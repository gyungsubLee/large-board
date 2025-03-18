package sub.board.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sub.board.comment.service.CommentService;
import sub.board.comment.service.request.CommentCreateRequest;
import sub.board.comment.service.response.CommentResponse;

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
}