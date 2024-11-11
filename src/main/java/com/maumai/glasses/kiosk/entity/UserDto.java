package com.maumai.glasses.kiosk.entity;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDto {
    @Column
    private String glassesFrame;

    @Column
    private String glassesColor;

    @Column
    private String faceShape;

    @Column
    private String personalColor;

}
