package com.agrishopper.controller;

import com.agrishopper.common.ApiResponse;
import com.agrishopper.dto.BatchFavoriteRequest;
import com.agrishopper.dto.FavoriteRequest;
import com.agrishopper.model.Favorite;
import com.agrishopper.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 收藏管理控制器
 * 提供收藏相关的REST API接口
 */
@Tag(name = "收藏管理", description = "收藏管理相关接口，包括添加收藏、取消收藏、查询收藏等操作")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "添加收藏", description = "用户添加商品到收藏")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "收藏成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "该商品已经收藏过了")
    })
    @PostMapping("/add")
    public ApiResponse<Favorite> addFavorite(@RequestBody FavoriteRequest request) {
        try {
            if (request.getUserId() == null || request.getProductCode() == null) {
                return ApiResponse.error(400, "用户ID和商品编号不能为空");
            }
            
            Favorite favorite = favoriteService.addFavorite(request.getUserId(), request.getProductCode());
            return ApiResponse.success(favorite);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "取消收藏", description = "用户取消收藏商品")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "取消收藏成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "收藏记录不存在")
    })
    @DeleteMapping("/remove")
    public ApiResponse<String> removeFavorite(@RequestBody FavoriteRequest request) {
        try {
            if (request.getUserId() == null || request.getProductCode() == null) {
                return ApiResponse.error(400, "用户ID和商品编号不能为空");
            }
            
            favoriteService.removeFavorite(request.getUserId(), request.getProductCode());
            return ApiResponse.success("取消收藏成功");
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "批量取消收藏", description = "用户批量取消收藏商品")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量取消收藏成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数有误")
    })
    @DeleteMapping("/batch-remove")
    public ApiResponse<String> removeFavorites(@RequestBody BatchFavoriteRequest request) {
        try {
            if (request.getUserId() == null || request.getProductCodes() == null || request.getProductCodes().isEmpty()) {
                return ApiResponse.error(400, "用户ID和商品编号列表不能为空");
            }
            
            favoriteService.removeFavorites(request.getUserId(), request.getProductCodes());
            return ApiResponse.success("批量取消收藏成功");
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "获取用户收藏列表", description = "获取指定用户的所有收藏")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取收藏列表"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户ID不能为空")
    })
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Favorite>> getUserFavorites(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            
            List<Favorite> favorites = favoriteService.getUserFavoritesOrderByCreateTime(userId);
            return ApiResponse.success(favorites);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "分页获取用户收藏列表", description = "分页获取指定用户的收藏")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取分页收藏列表"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户ID不能为空")
    })
    @GetMapping("/user/{userId}/page")
    public ApiResponse<Map<String, Object>> getUserFavoritesPage(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Favorite> favoritePage = favoriteService.getUserFavoritesOrderByCreateTime(userId, pageable);
            
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", favoritePage.getContent());
            pageData.put("totalElements", favoritePage.getTotalElements());
            pageData.put("totalPages", favoritePage.getTotalPages());
            pageData.put("currentPage", page);
            pageData.put("size", size);
            
            return ApiResponse.success(pageData);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "检查是否已收藏", description = "检查用户是否已收藏指定商品")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功检查收藏状态"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数不能为空")
    })
    @GetMapping("/check")
    public ApiResponse<Map<String, Boolean>> checkFavorite(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "商品编号") @RequestParam String productCode) {
        try {
            if (userId == null || productCode == null) {
                return ApiResponse.error(400, "用户ID和商品编号不能为空");
            }
            
            boolean isFavorited = favoriteService.isProductFavorited(userId, productCode);
            Map<String, Boolean> result = new HashMap<>();
            result.put("isFavorited", isFavorited);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "获取用户收藏数量", description = "获取指定用户的收藏总数")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取收藏数量"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户ID不能为空")
    })
    @GetMapping("/user/{userId}/count")
    public ApiResponse<Map<String, Long>> getUserFavoriteCount(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            
            long count = favoriteService.getUserFavoriteCount(userId);
            Map<String, Long> result = new HashMap<>();
            result.put("count", count);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "获取商品收藏数量", description = "获取指定商品被收藏的总数")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商品收藏数量"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "商品编号不能为空")
    })
    @GetMapping("/product/{productCode}/count")
    public ApiResponse<Map<String, Long>> getProductFavoriteCount(
            @Parameter(description = "商品编号") @PathVariable String productCode) {
        try {
            if (productCode == null) {
                return ApiResponse.error(400, "商品编号不能为空");
            }
            
            long count = favoriteService.getProductFavoriteCount(productCode);
            Map<String, Long> result = new HashMap<>();
            result.put("count", count);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "清空用户收藏", description = "清空指定用户的所有收藏")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "清空收藏成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户ID不能为空")
    })
    @DeleteMapping("/user/{userId}/clear")
    public ApiResponse<String> clearUserFavorites(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            
            favoriteService.clearUserFavorites(userId);
            return ApiResponse.success("清空收藏成功");
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @Operation(summary = "获取收藏详情", description = "根据收藏ID获取收藏详情")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取收藏详情"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "收藏记录不存在")
    })
    @GetMapping("/{favoriteId}")
    public ApiResponse<Favorite> getFavoriteById(
            @Parameter(description = "收藏ID") @PathVariable Long favoriteId) {
        try {
            if (favoriteId == null) {
                return ApiResponse.error(400, "收藏ID不能为空");
            }
            
            Favorite favorite = favoriteService.getFavoriteById(favoriteId);
            return ApiResponse.success(favorite);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
} 