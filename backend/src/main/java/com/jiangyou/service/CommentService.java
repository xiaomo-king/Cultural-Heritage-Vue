package com.jiangyou.service;

import com.jiangyou.dto.CommentRequest;
import com.jiangyou.model.Comment;
import com.jiangyou.repository.CommentRepository;
import com.jiangyou.repository.PostRepository;
import com.jiangyou.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public CommentService(CommentRepository cr, PostRepository pr, UserRepository ur) {
        this.commentRepository = cr; this.postRepository = pr; this.userRepository = ur;
    }

    public Comment createComment(Long userId, Long postId, CommentRequest req) {
        Comment c = new Comment();
        c.setPostId(postId);
        c.setUserId(userId);
        c.setContent(req.getContent());
        c.setReplyToUserId(req.getReplyToUserId());
        c.setReplyToContent(req.getReplyToContent());
        c = commentRepository.save(c);
        postRepository.findById(postId).ifPresent(p -> {
            p.setCommentCount(p.getCommentCount() + 1);
            postRepository.save(p);
        });
        return c;
    }

    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public Page<Comment> getCommentsByPage(Long postId, int page, int size) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, PageRequest.of(page, size));
    }

    public void deleteComment(Long commentId, Long userId) {
        commentRepository.findById(commentId).ifPresent(c -> {
            if (c.getUserId().equals(userId)) {
                commentRepository.delete(c);
                postRepository.findById(c.getPostId()).ifPresent(p -> {
                    p.setCommentCount(Math.max(0, p.getCommentCount() - 1));
                    postRepository.save(p);
                });
            }
        });
    }

    // 管理端：删除任意评论
    public boolean adminDelete(Long commentId) {
        return commentRepository.findById(commentId).map(c -> {
            commentRepository.delete(c);
            postRepository.findById(c.getPostId()).ifPresent(p -> {
                p.setCommentCount(Math.max(0, p.getCommentCount() - 1));
                postRepository.save(p);
            });
            return true;
        }).orElse(false);
    }
}
