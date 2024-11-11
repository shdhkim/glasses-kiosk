package com.maumai.glasses.kiosk.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}