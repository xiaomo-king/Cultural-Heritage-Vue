package com.jiangyou.controller.admin;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.dto.PageResponse;
import com.jiangyou.model.Post;
import com.jiangyou.model.User;
import com.jiangyou.repository.UserRepository;
import com.jiangyou.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/post")
public class PostAdminController {

    private final PostService postService;
    private final UserRepository userRepository;

    public PostAdminController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Map<String, Object>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> result = postService.adminList(page, size);
        List<Map<String, Object>> postList = new ArrayList<>();
        for (Post post : result.getContent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("post", post);
            // 附加用户信息
            userRepository.findById(post.getUserId()).ifPresent(u -> {
                map.put("nickName", u.getNickName());
                map.put("avatarUrl", u.getAvatarUrl());
            });
            postList.add(map);
        }
        return ApiResponse.success(new PageResponse<>(postList, page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Post> detail(@PathVariable Long id) {
        Post post = postService.getPostDetail(id);
        return post != null ? ApiResponse.success(post) : ApiResponse.error("打卡不存在");
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        return postService.adminDelete(id) ? ApiResponse.success("删除成功", null) : ApiResponse.error("打卡不存在");
    }
}
