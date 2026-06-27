package com.algovista.favorite.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.algovista.algorithm.entity.Algorithm;
import com.algovista.algorithm.service.AlgorithmService;
import com.algovista.favorite.entity.Favorite;
import com.algovista.favorite.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    private final FavoriteMapper favoriteMapper;
    private final AlgorithmService algorithmService;

    public FavoriteService(FavoriteMapper favoriteMapper, AlgorithmService algorithmService) {
        this.favoriteMapper = favoriteMapper;
        this.algorithmService = algorithmService;
    }

    public List<Algorithm> listFavorites(Long userId) {
        return favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt)
                        .orderByDesc(Favorite::getId))
                .stream()
                .map(Favorite::getAlgorithmId)
                .map(algorithmService::getAlgorithm)
                .toList();
    }

    public boolean isFavorite(Long userId, Long algorithmId) {
        return favoriteMapper.exists(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getAlgorithmId, algorithmId));
    }

    public void addFavorite(Long userId, Long algorithmId) {
        algorithmService.getAlgorithm(algorithmId);
        if (isFavorite(userId, algorithmId)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setAlgorithmId(algorithmId);
        favoriteMapper.insert(favorite);
    }

    public void removeFavorite(Long userId, Long algorithmId) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getAlgorithmId, algorithmId));
    }
}
