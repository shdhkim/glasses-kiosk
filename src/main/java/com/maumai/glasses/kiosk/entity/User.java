package com.maumai.glasses.kiosk.entity;

import com.maumai.glasses.kiosk.role.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Column(nullable = false)
    private String faceShape;

    @Column(nullable = false)
    private String personalColor;

    @Lob
    private byte[] userImage;

    @Column(nullable = false)
    private String feedBack;

    @Column(nullable = false)
    private String glassesFrame;

    @Column(nullable = false)
    private String glassesColor;

    private UserRole role; // USER

}
