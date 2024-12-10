package com.hx.pojo.DTO.forcegraph;

import lombok.Data;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.pojo.DTO
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-10  15:21
 * @Description: 用户图表DTO
 * @Version: 1.0
 */

@Data
public class UserTablesDTO {
    private Long id;
    private String name;
    private String imgUrl;
    private String description;
}
