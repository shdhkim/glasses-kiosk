package com.maumai.glasses.kiosk.controller;

import com.maumai.glasses.kiosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RootController {

    private final UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "Glasses Kiosk Application is running.";
    }

    // 💬 추가! DB 유저 수 체크용 API
    @GetMapping("/db-check")
    public String checkDatabase() {
        try {
            long count = userRepository.count(); // 유저 수를 가져옴

            return "Database is connected. Total Users: " + count;
        } catch (Exception e) {
            // DB 연결 자체가 안 될 때만 에러 처리
            return "Database connection failed.";
        }
    }
}