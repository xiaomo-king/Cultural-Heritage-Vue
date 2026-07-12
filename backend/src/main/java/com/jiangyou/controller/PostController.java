package com.jiangyou.controller;

import com.jiangyou.dto.*;
import com.jiangyou.model.Comment;
import com.jiangyou.model.Post;
import com.jiangyou.model.User;
import com.jiangyou.repository.UserRepository;
import com.jiangyou.service.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final UserRepository userRepository;

    private static final String IMAGE_BASE_URL = "http://localhost:8080";

    public PostController(PostService ps, CommentService cs, LikeService ls, UserRepository ur) {
        this.postService = ps; this.commentService = cs; this.likeService = ls; this.userRepository = ur;
    }

    // 将相对图片路径转为完整URL
    private List<String> toFullImageUrls(String images) {
        if (images == null || images.isEmpty()) return new ArrayList<>();
        return Arrays.stream(images.split(","))
                .filter(s -> !s.isEmpty())
                .map(url -> url.startsWith("/") ? IMAGE_BASE_URL + url : url)
                .collect(Collectors.toList());
    }

    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getList(
            @RequestParam(required = false) String topic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId) {
        Page<Post> result = postService.getPostList(topic, userId, page, size);
        List<Map<String, Object>> list = result.getContent().stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("content", p.getContent());
            m.put("title", p.getTitle());
            m.put("images", toFullImageUrls(p.getImages()));
            m.put("heritageName", p.getHeritageName());
            m.put("locationName", p.getLocationName());
            m.put("likeCount", p.getLikeCount());
            m.put("commentCount", p.getCommentCount());
            m.put("createdAt", p.getCreatedAt());
            m.put("topic", p.getTopic());
            m.put("visibility", p.getVisibility());
            // 用户信息
            userRepository.findById(p.getUserId()).ifPresent(u -> {
                m.put("userId", u.getId());
                m.put("nickName", u.getNickName());
                m.put("avatarUrl", u.getAvatarUrl());
            });
            // 是否已点赞
            if (userId != null) {
                m.put("isLiked", likeService.isLiked(userId, p.getId()));
            }
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", result.getTotalElements());
        data.put("page", page);
        data.put("size", size);
        data.put("totalPages", result.getTotalPages());
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getDetail(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        Post p = postService.getPostDetail(id);
        if (p == null) return ApiResponse.error("笔记不存在");
        Map<String, Object> m = new HashMap<>();
        m.put("id", p.getId()); m.put("content", p.getContent()); m.put("title", p.getTitle());
        m.put("images", toFullImageUrls(p.getImages()));
        m.put("heritageId", p.getHeritageId()); m.put("heritageName", p.getHeritageName());
        m.put("locationName", p.getLocationName()); m.put("latitude", p.getLatitude()); m.put("longitude", p.getLongitude());
        m.put("tags", p.getTags() != null && !p.getTags().isEmpty() ? Arrays.asList(p.getTags().split(",")) : new ArrayList<>());
        m.put("topic", p.getTopic()); m.put("likeCount", p.getLikeCount()); m.put("commentCount", p.getCommentCount());
        m.put("createdAt", p.getCreatedAt());
        userRepository.findById(p.getUserId()).ifPresent(u -> {
            m.put("userId", u.getId()); m.put("nickName", u.getNickName()); m.put("avatarUrl", u.getAvatarUrl());
        });
        if (userId != null) m.put("isLiked", likeService.isLiked(userId, p.getId()));

        // 评论
        List<Comment> comments = commentService.getComments(id);
        List<Map<String, Object>> commentList = comments.stream().map(c -> {
            Map<String, Object> cm = new HashMap<>();
            cm.put("id", c.getId()); cm.put("content", c.getContent()); cm.put("createdAt", c.getCreatedAt());
            cm.put("replyToUserId", c.getReplyToUserId()); cm.put("replyToContent", c.getReplyToContent());
            userRepository.findById(c.getUserId()).ifPresent(u -> {
                cm.put("userId", u.getId()); cm.put("nickName", u.getNickName()); cm.put("avatarUrl", u.getAvatarUrl());
            });
            return cm;
        }).collect(Collectors.toList());
        m.put("comments", commentList);
        return ApiResponse.success(m);
    }

    @PostMapping("/create")
    public ApiResponse<?> create(@RequestHeader("userId") Long userId, @RequestBody PostRequest req) {
        Post post = postService.createPost(userId, req);
        return ApiResponse.success("发布成功", Map.of("postId", post.getId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(@RequestHeader("userId") Long userId, @PathVariable Long id, @RequestBody PostRequest req) {
        Post post = postService.updatePost(id, userId, req);
        if (post == null) return ApiResponse.error("编辑失败，找不到笔记或无权限");
        return ApiResponse.success("编辑成功", Map.of("postId", post.getId()));
    }

    @GetMapping("/liked")
    public ApiResponse<?> getLikedPosts(@RequestHeader("userId") Long userId) {
        List<Long> postIds = likeService.getUserLikedPostIds(userId);
        if (postIds.isEmpty()) return ApiResponse.success(Map.of("list", new ArrayList<>()));
        List<Post> posts = postService.findAllById(postIds);
        List<Map<String, Object>> list = posts.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("content", p.getContent());
            m.put("title", p.getTitle());
            m.put("images", toFullImageUrls(p.getImages()));
            m.put("heritageName", p.getHeritageName());
            m.put("likeCount", p.getLikeCount());
            m.put("createdAt", p.getCreatedAt());
            userRepository.findById(p.getUserId()).ifPresent(u -> {
                m.put("userId", u.getId());
                m.put("nickName", u.getNickName());
            });
            return m;
        }).collect(Collectors.toList());
        return ApiResponse.success(Map.of("list", list));
    }

    @PostMapping("/{id}/like")
    public ApiResponse<?> toggleLike(@RequestHeader("userId") Long userId, @PathVariable Long id) {
        boolean liked = likeService.toggleLike(userId, id);
        return ApiResponse.success(Map.of("isLiked", liked));
    }

    @PostMapping("/{id}/comment")
    public ApiResponse<?> addComment(@RequestHeader("userId") Long userId, @PathVariable Long id, @RequestBody CommentRequest req) {
        Comment c = commentService.createComment(userId, id, req);
        return ApiResponse.success("评论成功", Map.of("commentId", c.getId()));
    }

    /** 分页获取评论 */
    @GetMapping("/{id}/comments")
    public ApiResponse<PageResponse<Map<String, Object>>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Comment> result = commentService.getCommentsByPage(id, page, size);
        List<Map<String, Object>> commentList = result.getContent().stream().map(c -> {
            Map<String, Object> cm = new HashMap<>();
            cm.put("id", c.getId()); cm.put("content", c.getContent()); cm.put("createdAt", c.getCreatedAt());
            cm.put("replyToUserId", c.getReplyToUserId()); cm.put("replyToContent", c.getReplyToContent());
            userRepository.findById(c.getUserId()).ifPresent(u -> {
                cm.put("userId", u.getId()); cm.put("nickName", u.getNickName()); cm.put("avatarUrl", u.getAvatarUrl());
            });
            return cm;
        }).collect(Collectors.toList());
        return ApiResponse.success(new PageResponse<>(commentList, page, size, result.getTotalElements()));
    }

    @GetMapping("/search")
    public ApiResponse<?> search(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Page<Post> result = postService.searchPosts(keyword, page, size);
        List<Map<String, Object>> list = result.getContent().stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("content", p.getContent());
            m.put("title", p.getTitle());
            m.put("images", toFullImageUrls(p.getImages()));
            m.put("heritageName", p.getHeritageName());
            m.put("likeCount", p.getLikeCount());
            m.put("commentCount", p.getCommentCount());
            m.put("createdAt", p.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ApiResponse.success(new PageResponse<>(list, page, size, result.getTotalElements()));
    }
}
