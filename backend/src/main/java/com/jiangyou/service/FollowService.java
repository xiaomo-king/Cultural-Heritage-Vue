package com.jiangyou.service;

import com.jiangyou.model.Follow;
import com.jiangyou.repository.FollowRepository;
import com.jiangyou.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    public FollowService(FollowRepository fr, UserRepository ur) { this.followRepository = fr; this.userRepository = ur; }

    @Transactional
    public boolean toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) return false;
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            userRepository.findById(followerId).ifPresent(u -> {
                u.setFollowCount(Math.max(0, u.getFollowCount() - 1));
                userRepository.save(u);
            });
            userRepository.findById(followingId).ifPresent(u -> {
                u.setFollowerCount(Math.max(0, u.getFollowerCount() - 1));
                userRepository.save(u);
            });
            return false;
        } else {
            Follow f = new Follow(); f.setFollowerId(followerId); f.setFollowingId(followingId);
            followRepository.save(f);
            userRepository.findById(followerId).ifPresent(u -> {
                u.setFollowCount(u.getFollowCount() + 1);
                userRepository.save(u);
            });
            userRepository.findById(followingId).ifPresent(u -> {
                u.setFollowerCount(u.getFollowerCount() + 1);
                userRepository.save(u);
            });
            return true;
        }
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public List<Long> getFollowings(Long userId) {
        return followRepository.findByFollowerId(userId).stream().map(Follow::getFollowingId).collect(Collectors.toList());
    }

    public List<Long> getFollowers(Long userId) {
        return followRepository.findByFollowingId(userId).stream().map(Follow::getFollowerId).collect(Collectors.toList());
    }
}
