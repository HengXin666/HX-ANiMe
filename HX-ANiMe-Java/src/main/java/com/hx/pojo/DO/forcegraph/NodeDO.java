package com.hx.pojo.DO.forcegraph;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.pojo.DO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-17  22:27
 * @Description: 力导向图结点
 * @Version: 1.0
 */

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "Nodes")
@Data
public class NodeDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nodeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long userTableId;

    @Column(nullable = false)
    private Long legendId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String imgUrl;

    @Column(columnDefinition = "TEXT")
    private String description;
}

