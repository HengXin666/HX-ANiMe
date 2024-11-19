package com.hx.pojo.DO.forcegraph;

import lombok.Data;

import javax.persistence.*;

/**
 * @description: 图例
 *
 * @author: Heng_Xin
 * @date: 2024/11/19 16:39
 **/

@Data
@Entity
@Table(name = "Legends")
public class LegendDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long legendId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long userTableId;

    @Column(nullable = false, length = 255)
    private String legendName;

    @Column(nullable = false, length = 50)
    private String legendColor;
}