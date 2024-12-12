package com.hx.pojo.DO.forcegraph;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.pojo.DO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-12  15:36
 * @Description: 图表密钥
 * @Version: 1.0
 */

@Data
@Entity
@Table(
    name = "GraphApiKeys",
    uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "userTableId"})
)
public class GraphApiKeyDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long graphApiKeyId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long userTableId;

    @Column(nullable = false, length = 64)
    private String apiKey; // md5

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;
}
