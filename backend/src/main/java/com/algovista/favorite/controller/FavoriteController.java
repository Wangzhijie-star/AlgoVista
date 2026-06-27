package com.algovista.favorite.controller;

import com.algovista.algorithm.entity.Algorithm;
import com.algovista.auth.security.SessionKeys;
import com.algovista.common.result.ApiResponse;
import com.algovista.favorite.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ApiResponse<List<Algorithm>> list(HttpSession session) {
        return ApiResponse.ok(favoriteService.listFavorites(currentUserId(session)));
    }

    @GetMapping("/{algorithmId}")
    public ApiResponse<FavoriteStatusResponse> status(@PathVariable Long algorithmId, HttpSession session) {
        return ApiResponse.ok(new FavoriteStatusResponse(favoriteService.isFavorite(currentUserId(session), algorithmId)));
    }

    @PostMapping
    public ApiResponse<Void> add(@Valid @RequestBody FavoriteRequest request, HttpSession session) {
        favoriteService.addFavorite(currentUserId(session), request.algorithmId());
        return ApiResponse.ok();
    }

    @DeleteMapping("/{algorithmId}")
    public ApiResponse<Void> remove(@PathVariable Long algorithmId, HttpSession session) {
        favoriteService.removeFavorite(currentUserId(session), algorithmId);
        return ApiResponse.ok();
    }

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
    }

    public record FavoriteRequest(@NotNull Long algorithmId) {
    }

    public record FavoriteStatusResponse(boolean favorite) {
    }
}
