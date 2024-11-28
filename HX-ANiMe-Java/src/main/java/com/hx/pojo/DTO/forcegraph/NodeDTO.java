package com.hx.pojo.DTO.forcegraph;

import lombok.Data;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.pojo.DTO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-22  09:36
 * @Description: 结点传输对象
 * @Version: 1.0
 */

@Data
public class NodeDTO {
    private Long nodeId; // 用户传入时候, 该项无用
    private Long legendId;
    private String name;
    private String imgUrl;
    private String description;
}
