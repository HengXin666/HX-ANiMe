package com.hx.pojo.DO.forcegraph;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Edges")
public class EdgeDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer edgeId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer userTableId;

    @Column(nullable = false)
    private Integer fromNodeId;

    @Column(nullable = false)
    private Integer toNodeId;
}