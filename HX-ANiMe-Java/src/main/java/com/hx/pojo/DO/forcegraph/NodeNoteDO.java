package com.hx.pojo.DO.forcegraph;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.pojo.DO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-17  22:36
 * @Description: TODO
 * @Version: 1.0
 */

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "NodeNotes")
public class NodeNoteDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long userTableId;

    @Column(nullable = false)
    private Long nodeId;

    @Column(nullable = false, length = 255)
    private String noteKey;

    @Column(columnDefinition = "TEXT")
    private String noteValue;
}

