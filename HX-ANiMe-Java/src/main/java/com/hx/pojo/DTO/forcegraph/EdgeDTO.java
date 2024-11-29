package com.hx.pojo.DTO.forcegraph;

import lombok.Data;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.DTO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-29  09:10
 * @Description: 边的传输对象
 * @Version: 1.0
 */
@Data
public class EdgeDTO {
    private Long edgeId; // 用户传入时候, 该项无用
    private Long fromNodeId;
    private Long toNodeId;
}
