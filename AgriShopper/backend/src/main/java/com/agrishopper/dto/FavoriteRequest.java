package com.agrishopper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 收藏请求DTO
 */
@Schema(description = "收藏请求参数")
@Data
public class FavoriteRequest {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "商品编号", example = "507233")
    private String productCode;
} 