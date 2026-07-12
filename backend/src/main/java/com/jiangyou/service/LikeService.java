package com.jiangyou.service;

import com.jiangyou.model.Likes;
import com.jiangyou.repository.LikesRepository;
import com.jiangyou.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {
    private final LikesRepository likesRepository;
    private final PostRepository postRepository;
    public LikeService(LikesRepository lr, PostRepository pr) { this.likesRepository = lr; this.postRepository = pr; }

    @Transactional
    public boolean toggleLike(Long userId, Long postId) {
        if (likesRepository.existsByPostIdAndUserId(postId, userId)) {
            likesRepository.deleteByPostIdAndUserId(postId, userId);
            postRepository.findById(postId).ifPresent(p -> {
                p.setLikeCount(Math.max(0, p.getLikeCount() - 1));
                postRepository.save(p);
            });
            return false;
        } else {
            Likes like = new Likes();
            like.setPostId(postId); like.setUserId(userId);
            likesRepository.save(like);
            postRepository.findById(postId).ifPresent(p -> {
                p.setLikeCount(p.getLikeCount() + 1);
                postRepository.save(p);
            });
            return true;
        }
    }

    public boolean isLiked(Long userId, Long postId) {
        return likesRepository.existsByPostIdAndUserId(postId, userId);
    }

    // 获取用户点赞过的所有帖子ID
    public List<Long> getUserLikedPostIds(Long userId) {
        return likesRepository.findByUserId(userId).stream()
                .map(Likes::getPostId)
                .collect(java.util.stream.Collectors.toList());
    }
}
