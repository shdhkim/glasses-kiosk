package com.maumai.glasses.kiosk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String image_path_edited;

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

    private Double width;

    @Column
    private Double length;

    @Column
    private Double weight;

    @JsonManagedReference
    @OneToMany(mappedBy = "glasses", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlassesRecommend> glassesRecommendList = new ArrayList<>();


}