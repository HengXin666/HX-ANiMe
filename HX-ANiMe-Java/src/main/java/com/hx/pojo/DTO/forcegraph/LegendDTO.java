package com.hx.pojo.DTO.forcegraph;

import lombok.Data;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.pojo.DTO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-19  16:43
 * @Description: 图例DTO
 * @Version: 1.0
 */

@Data
public class LegendDTO {
    private Long legendId; // 用户传入时候, 该项无用
    private String legendName;
    private String legendColor;
}
