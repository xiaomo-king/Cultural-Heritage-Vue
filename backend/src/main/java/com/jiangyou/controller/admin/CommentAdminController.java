package com.jiangyou.controller.admin;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.dto.PageResponse;
import com.jiangyou.model.Comment;
import com.jiangyou.repository.CommentRepository;
import com.jiangyou.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comment")
public class CommentAdminController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    public CommentAdminController(CommentService commentService, CommentRepository commentRepository) {
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/list")
    public ApiResponse<?> list(
            @RequestParam(required = false) Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (postId != null) {
            Page<Comment> result = commentRepository.findByPostIdOrderByCreatedAtAsc(postId, PageRequest.of(page, size));
            return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
        }
        // 不传postId返回全部（分页）
        Page<Comment> result = commentRepository.findAll(PageRequest.of(page, size));
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        return commentService.adminDelete(id) ? ApiResponse.success("删除成功", null) : ApiResponse.error("评论不存在");
    }
}
