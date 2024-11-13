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

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Column
    private String productName;

    @Column
    private Double price;

    @Column
    private String brand;

    @Column
    private String color;

    @Column
    private String size;

    @OneToMany(mappedBy = "glasses", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GlassesRecommend> glassesRecommendList = new ArrayList<>();
}