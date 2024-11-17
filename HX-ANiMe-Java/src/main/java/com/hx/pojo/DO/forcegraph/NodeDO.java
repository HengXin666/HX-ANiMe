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
    private Integer nodeId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer userTableId;

    @Column(nullable = false)
    private Integer legendId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String imgUrl;

    @Column(columnDefinition = "TEXT")
    private String description;
}

