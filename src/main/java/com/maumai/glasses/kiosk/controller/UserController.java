package com.maumai.glasses.kiosk.controller;

import com.maumai.glasses.kiosk.entity.User;
import com.maumai.glasses.kiosk.repository.UserRepository;
import com.maumai.glasses.kiosk.response.Response;
import com.maumai.glasses.kiosk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Controller
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "사진 저장", description = "사진을 저장한다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/image/save/{userid}")
    public Response imageSave(@PathVariable Long userId, @RequestPart("image") MultipartFile[] files) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (files[0].isEmpty()) {
            files[0] = null;
        }

        return new Response("성공", "이미지 저장 성공", userService.save(user, files));
    }
    @Operation(summary = "사진 전송", description = "사진을 전송한다.")
    @GetMapping("/image/send/{userid}")
    public ResponseEntity<?> imageSend(@PathVariable("userId") Long userId) {
        List<byte[]> downloadImage = userService.send(userId);
        return ResponseEntity.ok(downloadImage);
    }

    @Operation(summary = "피드백 저장", description = "피드백을 저장한다.")
    @PostMapping("/feedback/save")
    public Response saveFeedBack(@RequestPart("userId") Long userId, @RequestPart("feedBack") String feedBack) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new Response("성공", "피드백 저장 성공", userService.saveFeedBack(user, feedBack));
    }

    @Operation(summary = "피드백 리턴", description = "피드백을 리턴한다.")
    @PostMapping("/feedback/return")
    public Response returnFeedBack(@RequestPart("userId") Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new Response("리턴", "피드백 리턴 성공", userService.returnFeedBack(user));
    }

    @Operation(summary="유저 정보 찾기", description = "유저 정보를 리턴한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/find/{userid}")
    public Response<?> findUser(@PathVariable("userid") Long userid) {
        return new Response<>("true", "조회 성공", userService.findUser(userid));
    }
}
