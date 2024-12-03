package com.maumai.glasses.kiosk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.maumai.glasses.kiosk.role.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class User {

    @Id
    private Long userId;

    @Column
    private String faceShape;

    @Column
    private String personalColor;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] userImage;

    @Column
    private String feedBack;

    @Column
    private String glassesFrame;

    @Column
    private String glassesColor;

    private UserRole role; // USER

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlassesRecommend> glassesRecommendList = new ArrayList<>();
}