package com.hx.pojo.DO.forcegraph;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Legends")
public class LegendDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer legendId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer userTableId;

    @Column(nullable = false, length = 255)
    private String legendName;

    @Column(nullable = false, length = 50)
    private String legendColor;
}