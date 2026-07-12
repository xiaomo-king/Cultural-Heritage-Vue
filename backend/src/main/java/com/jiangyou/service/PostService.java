package com.jiangyou.service;

import com.jiangyou.dto.PostRequest;
import com.jiangyou.model.Post;
import com.jiangyou.repository.PostRepository;
import com.jiangyou.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Page<Post> getPostList(String topic, Long userId, int page, int size) {
        if (userId != null) {
            // 查看自己的：全部可见（public + private）
            return postRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, 1, PageRequest.of(page, size));
        }
        if (topic != null && !topic.isEmpty()) {
            return postRepository.findByTopicAndVisibilityAndStatus(topic, "public", 1, PageRequest.of(page, size));
        }
        // 首页瀑布流只展示公开的
        return postRepository.findByVisibilityAndStatusOrderByCreatedAtDesc("public", 1, PageRequest.of(page, size));
    }

    // 查看某个用户的公开打卡（他人主页用）
    public Page<Post> getUserPublicPosts(Long userId, int page, int size) {
        return postRepository.findByUserIdAndVisibilityAndStatusOrderByCreatedAtDesc(userId, "public", 1, PageRequest.of(page, size));
    }

    public Post getPostDetail(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post createPost(Long userId, PostRequest req) {
        Post post = new Post();
        post.setUserId(userId);
        post.setHeritageId(req.getHeritageId());
        post.setHeritageName(req.getHeritageName() != null ? req.getHeritageName() : "");
        post.setContent(req.getContent() != null ? req.getContent() : "");
        post.setTitle(req.getTitle() != null ? req.getTitle() : "");
        post.setImages(req.getImages() != null ? String.join(",", req.getImages()) : "");
        post.setLocationName(req.getLocationName() != null ? req.getLocationName() : "");
        post.setLatitude(req.getLatitude() != null ? java.math.BigDecimal.valueOf(req.getLatitude()) : null);
        post.setLongitude(req.getLongitude() != null ? java.math.BigDecimal.valueOf(req.getLongitude()) : null);
        post.setTags(req.getTags() != null ? String.join(",", req.getTags()) : "");
        post.setTopic(req.getTopic() != null ? req.getTopic() : "");
        post.setVisibility(req.getVisibility() != null ? req.getVisibility() : "public");
        post = postRepository.save(post);
        userRepository.findById(userId).ifPresent(u -> {
            u.setCheckinCount(u.getCheckinCount() + 1);
            userRepository.save(u);
        });
        return post;
    }

    // 编辑打卡笔记
    public Post updatePost(Long postId, Long userId, PostRequest req) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return null;
        if (!post.getUserId().equals(userId)) return null; // 只能编辑自己的
        if (req.getContent() != null) post.setContent(req.getContent());
        if (req.getTitle() != null) post.setTitle(req.getTitle());
        if (req.getImages() != null) post.setImages(String.join(",", req.getImages()));
        if (req.getLocationName() != null) post.setLocationName(req.getLocationName());
        if (req.getLatitude() != null) post.setLatitude(java.math.BigDecimal.valueOf(req.getLatitude()));
        if (req.getLongitude() != null) post.setLongitude(java.math.BigDecimal.valueOf(req.getLongitude()));
        if (req.getTags() != null) post.setTags(String.join(",", req.getTags()));
        if (req.getTopic() != null) post.setTopic(req.getTopic());
        if (req.getHeritageId() != null) post.setHeritageId(req.getHeritageId());
        if (req.getHeritageName() != null) post.setHeritageName(req.getHeritageName());
        if (req.getVisibility() != null) post.setVisibility(req.getVisibility());
        return postRepository.save(post);
    }

    public List<Post> findAllById(List<Long> ids) {
        return postRepository.findAllById(ids);
    }

    public Page<Post> searchPosts(String keyword, int page, int size) {
        return postRepository.search(keyword, PageRequest.of(page, size));
    }

    // ===== 管理端方法 =====
    public Page<Post> adminList(int page, int size) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    public boolean adminDelete(Long postId) {
        if (!postRepository.existsById(postId)) return false;
        postRepository.deleteById(postId);
        return true;
    }
}
