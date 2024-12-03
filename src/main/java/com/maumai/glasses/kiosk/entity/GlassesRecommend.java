package com.maumai.glasses.kiosk.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class GlassesRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "glasses_id", nullable = false)
    private Glasses glasses;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] mixImage;

}