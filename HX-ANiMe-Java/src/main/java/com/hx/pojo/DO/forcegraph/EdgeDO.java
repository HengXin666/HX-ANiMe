package com.hx.pojo.DO.forcegraph;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Edges")
public class EdgeDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long edgeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long userTableId;

    @Column(nullable = false)
    private Long fromNodeId;

    @Column(nullable = false)
    private Long toNodeId;
}