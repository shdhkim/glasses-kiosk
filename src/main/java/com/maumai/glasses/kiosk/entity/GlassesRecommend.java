package com.maumai.glasses.kiosk.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
public class GlassesRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "glasses_id", nullable = false)
    private Glasses glasses;


}