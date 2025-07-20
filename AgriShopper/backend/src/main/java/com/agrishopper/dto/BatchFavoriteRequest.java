package com.agrishopper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 批量收藏请求DTO
 */
@Schema(description = "批量收藏请求参数")
@Data
public class BatchFavoriteRequest {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "商品编号列表")
    private List<String> productCodes;
} 