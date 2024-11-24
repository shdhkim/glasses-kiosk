package com.maumai.glasses.kiosk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Glasses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String image_path;

    @Column
    private String model;

    @Column
    private Double price;

    @Column
    private String brand;

    @Column
    private String shape;

    @Column
    private String material;

    @Column
    private String color;

    @Column
    private Double width;

    @Column
    private Double length;

    @Column
    private Double weight;

    @OneToMany(mappedBy = "glasses", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GlassesRecommend> glassesRecommendList = new ArrayList<>();
}