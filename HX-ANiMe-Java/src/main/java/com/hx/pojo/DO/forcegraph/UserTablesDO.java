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
@Table(name = "user_tables")
@Data
public class UserTablesDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "img_url", length = 500)
    private String imgUrl;

    @Column(name = "description", nullable = false, length = 255)
    private String description;
}
