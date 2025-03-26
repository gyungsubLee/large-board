package sub.board.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sub.board.comment.service.CommentServiceV2;
import sub.board.comment.service.request.CommentCreateRequestV2;
import sub.board.comment.service.response.CommentResponse;

// TODO: 응답 포맷 수정
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/comments")
public class CommentControllerV2 {
    private final CommentServiceV2 commentService;

    @PostMapping
    public CommentResponse create(@RequestBody CommentCreateRequestV2 request) {
        CommentResponse response = commentService.create(request);
        return response;
    }

    @GetMapping("/{commentId}")
    public CommentResponse read(@PathVariable Long commentId) {
        return commentService.read((commentId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok().body("정상 삭제 되었습니다.");
    }
}
