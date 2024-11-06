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

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Column
    private String faceShape;

    @Column
    private String personalColor;

    @Lob
    @Column
    private byte[] userImage;

    @Column
    private String feedBack;

    @Column
    private String glassesFrame;

    @Column
    private String glassesColor;

    private UserRole role; // USER

}
