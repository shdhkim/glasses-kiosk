package com.maumai.glasses.kiosk.controller;

import com.maumai.glasses.kiosk.entity.Glasses;
import com.maumai.glasses.kiosk.response.Response;
import com.maumai.glasses.kiosk.service.GlassesService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/glasses")
public class GlassesController {

    private final GlassesService glassesService;

    @Operation(summary = "유저 ID로 안경 데이터 조회", description = "지정된 유저 ID의 안경 데이터를 조회한다.")
    @GetMapping("find/{userId}")
    public ResponseEntity<Response<List<Glasses>>> getGlassesByUserId(@PathVariable Long userId) {
        return glassesService.getGlassesByUserId(userId);
    }
}