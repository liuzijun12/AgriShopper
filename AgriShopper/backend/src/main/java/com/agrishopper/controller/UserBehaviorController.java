package com.agrishopper.controller;

import com.agrishopper.model.UserBehavior;
import com.agrishopper.service.UserBehaviorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Tag(name = "用户行为记录", description = "用户行为记录和推荐系统相关接口")
@RestController
@RequestMapping("/api/user-behavior")
public class UserBehaviorController {

    private final UserBehaviorService userBehaviorService;

    @Autowired
    public UserBehaviorController(UserBehaviorService userBehaviorService) {
        this.userBehaviorService = userBehaviorService;
    }

    @Operation(summary = "记录用户行为", description = "记录用户的浏览、点击、购买等行为")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "行为记录成功"),
        @ApiResponse(responseCode = "400", description = "请求参数有误")
    })
    @PostMapping("/record")
    public ResponseEntity<?> recordBehavior(@RequestBody UserBehavior behavior) {
        try {
            UserBehavior savedBehavior = userBehaviorService.saveUserBehavior(behavior);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(savedBehavior));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取用户行为记录", description = "获取指定用户的行为记录列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取行为记录"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBehaviors(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            List<UserBehavior> behaviors = userBehaviorService.findByUserId(userId);
            return ResponseEntity.ok(createSuccessResponse(behaviors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取个性化推荐", description = "基于用户历史行为获取个性化商品推荐")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取推荐"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<?> getPersonalizedRecommendations(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<String> recommendations = userBehaviorService.getPersonalizedRecommendations(userId, limit);
            return ResponseEntity.ok(createSuccessResponse(recommendations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取协同过滤推荐", description = "基于相似用户行为获取协同过滤推荐")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取推荐"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/collaborative-filtering/{userId}")
    public ResponseEntity<?> getCollaborativeFilteringRecommendations(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<String> recommendations = userBehaviorService.getCollaborativeFilteringRecommendations(userId, limit);
            return ResponseEntity.ok(createSuccessResponse(recommendations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取热门推荐", description = "获取基于热度排序的商品推荐")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取热门推荐")
    })
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularRecommendations(
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "时间范围(天)") @RequestParam(defaultValue = "7") int days) {
        try {
            List<String> recommendations = userBehaviorService.getGlobalHotProducts(limit, days);
            return ResponseEntity.ok(createSuccessResponse(recommendations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取实时推荐", description = "获取基于实时行为的推荐")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取实时推荐")
    })
    @GetMapping("/realtime/{userId}")
    public ResponseEntity<?> getRealtimeRecommendations(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<String> recommendations = userBehaviorService.getRealTimeRecommendations(userId, limit);
            return ResponseEntity.ok(createSuccessResponse(recommendations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取用户行为统计", description = "获取用户行为统计分析数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取统计数据")
    })
    @GetMapping("/statistics/{userId}")
    public ResponseEntity<?> getBehaviorStatistics(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            Map<String, Object> statistics = userBehaviorService.getUserBehaviorStatistics(userId);
            return ResponseEntity.ok(createSuccessResponse(statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "记录查看商品行为", description = "记录用户查看商品的行为")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "记录成功")
    })
    @PostMapping("/view-product")
    public ResponseEntity<?> recordViewProduct(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "商品编码") @RequestParam String productCode,
            @Parameter(description = "商品名称") @RequestParam String productName,
            @Parameter(description = "来源页面") @RequestParam(defaultValue = "product-detail") String sourcePage) {
        try {
            userBehaviorService.recordViewProduct(userId, productCode, productName, sourcePage);
            return ResponseEntity.ok(createSuccessResponse("记录成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "记录点击商品行为", description = "记录用户点击商品的行为")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "记录成功")
    })
    @PostMapping("/click-product")
    public ResponseEntity<?> recordClickProduct(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "商品编码") @RequestParam String productCode,
            @Parameter(description = "商品名称") @RequestParam String productName,
            @Parameter(description = "来源页面") @RequestParam(defaultValue = "product-list") String sourcePage) {
        try {
            userBehaviorService.recordClickProduct(userId, productCode, productName, sourcePage);
            return ResponseEntity.ok(createSuccessResponse("记录成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "记录加入购物车行为", description = "记录用户加入购物车的行为")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "记录成功")
    })
    @PostMapping("/add-to-cart")
    public ResponseEntity<?> recordAddToCart(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "商品编码") @RequestParam String productCode,
            @Parameter(description = "商品名称") @RequestParam String productName,
            @Parameter(description = "来源页面") @RequestParam(defaultValue = "product-detail") String sourcePage) {
        try {
            userBehaviorService.recordAddToCart(userId, productCode, productName, sourcePage);
            return ResponseEntity.ok(createSuccessResponse("记录成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "记录收藏商品行为", description = "记录用户收藏商品的行为")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "记录成功")
    })
    @PostMapping("/add-to-favorite")
    public ResponseEntity<?> recordAddToFavorite(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "商品编码") @RequestParam String productCode,
            @Parameter(description = "商品名称") @RequestParam String productName,
            @Parameter(description = "来源页面") @RequestParam(defaultValue = "product-detail") String sourcePage) {
        try {
            userBehaviorService.recordAddToFavorite(userId, productCode, productName, sourcePage);
            return ResponseEntity.ok(createSuccessResponse("记录成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "记录搜索行为", description = "记录用户搜索商品的行为")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "记录成功")
    })
    @PostMapping("/search")
    public ResponseEntity<?> recordSearchProduct(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "来源页面") @RequestParam(defaultValue = "search") String sourcePage) {
        try {
            userBehaviorService.recordSearchProduct(userId, keyword, sourcePage);
            return ResponseEntity.ok(createSuccessResponse("记录成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "批量记录行为", description = "批量记录多个用户行为")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "批量记录成功"),
        @ApiResponse(responseCode = "400", description = "请求参数有误")
    })
    @PostMapping("/batch-record")
    public ResponseEntity<?> batchRecordBehaviors(@RequestBody List<UserBehavior> behaviors) {
        try {
            List<UserBehavior> savedBehaviors = userBehaviorService.saveUserBehaviors(behaviors);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(savedBehaviors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "删除用户行为记录", description = "删除指定用户的行为记录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "记录不存在")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBehavior(
            @Parameter(description = "行为记录ID") @PathVariable Long id) {
        try {
            userBehaviorService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "清理过期行为记录", description = "清理指定天数前的行为记录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "清理成功")
    })
    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanupOldBehaviors(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "90") int days) {
        try {
            userBehaviorService.cleanExpiredData(days);
            Map<String, Object> result = new HashMap<>();
            result.put("message", "清理完成");
            return ResponseEntity.ok(createSuccessResponse(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
} 