package com.hx.pojo.DO.forcegraph;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.pojo.DO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-12  15:36
 * @Description: 图表密钥
 * @Version: 1.0
 */

class GraphApiKeyId implements Serializable {
    private Long userId;
    private Long userTableId;
}

@Data
@Entity
@Table(name = "graph_api_keys")
public class GraphApiKeyDO {
    @EmbeddedId
    GraphApiKeyId id;

    @Column(name = "api_key", nullable = false, length = 64)
    private String apiKey; // md5

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}

