package com.maumai.glasses.kiosk.controller;

import com.maumai.glasses.kiosk.entity.User;
import com.maumai.glasses.kiosk.entity.UserDto;
import com.maumai.glasses.kiosk.response.Response;
import com.maumai.glasses.kiosk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "이미지 저장 및 Flask 전송", description = "이미지를 저장하고 Flask 서버로 전송한다.")
    @PostMapping("/image/save/{userId}")
    public ResponseEntity<Response<String>> imageSave(
            @PathVariable Long userId,
            @RequestPart("image") MultipartFile[] files) throws IOException {
        return userService.save(userId, files);
    }
    @Operation(summary = "유저 정보 조회", description = "유저 ID로 유저의 정보(UserDto)를 조회한다.")
    @GetMapping("/find/{userId}")
    public ResponseEntity<Response<UserDto>> getUserDtoById(@PathVariable Long userId) {
        return userService.getUserDtoById(userId);
    }
    @Operation(summary = "이미지 리턴", description = "이미지를 리턴한다.")
    @GetMapping("/image/send/{userId}")
    public ResponseEntity<List<byte[]>> imageSend(@PathVariable("userId") Long userId) {
        List<byte[]> downloadImage = userService.send(userId);
        return ResponseEntity.ok(downloadImage);
    }

    @Operation(summary = "유저 정보 찾기", description = "유저 정보를 리턴한다.")
    @GetMapping("findall/{userId}")
    public ResponseEntity<Response<User>> findUser(@PathVariable("userId") Long userId) {
        return userService.findUser(userId);
    }

    @Operation(summary = "피드백 저장 및 Flask 전송", description = "피드백을 저장하고 Flask 서버로 전송한다.")
    @PostMapping("/feedback/save")
    public ResponseEntity<Response<String>> sendFeedbackToFlask(
            @RequestParam("userId") Long userId,
            @RequestParam("feedback") String feedback) {
        return userService.sendFeedbackToFlask(userId, feedback);
    }
}