package com.hx.pojo.DO.forcegraph;

import lombok.Data;

import javax.persistence.*;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.pojo.DO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-06  10:48
 * @Description: 力导向图表数据表
 * @Version: 1.0
 */

@Entity
@Table(name = "UserTablesDO")
@Data
public class UserTablesDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_table_id", nullable = false)
    private Long userTableId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "table_name", nullable = false, length = 255)
    private String tableName;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Column(name = "table_content", columnDefinition = "TEXT")
    private String tableContent;
}
