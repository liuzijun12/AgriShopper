package com.agrishopper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "添加购物车请求")
@Data
public class AddToCartRequest {
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "商品ID", example = "1")
    private Long productId;
    
    @Schema(description = "商品数量", example = "1")
    private Integer quantity = 1;
} 