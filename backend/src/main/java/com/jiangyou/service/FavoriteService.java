package com.jiangyou.service;

import com.jiangyou.model.Favorite;
import com.jiangyou.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    public FavoriteService(FavoriteRepository fr) { this.favoriteRepository = fr; }

    public boolean toggleFavorite(Long userId, Long targetId, String targetType) {
        var opt = favoriteRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
        if (opt.isPresent()) {
            favoriteRepository.delete(opt.get());
            return false;
        } else {
            Favorite f = new Favorite();
            f.setUserId(userId); f.setTargetId(targetId); f.setTargetType(targetType);
            favoriteRepository.save(f);
            return true;
        }
    }

    public boolean isFavorited(Long userId, Long targetId, String targetType) {
        return favoriteRepository.existsByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
    }

    public List<Favorite> getFavorites(Long userId, String targetType) {
        return favoriteRepository.findByUserIdAndTargetType(userId, targetType);
    }
}
