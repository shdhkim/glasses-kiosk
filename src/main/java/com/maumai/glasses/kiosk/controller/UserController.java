package com.maumai.glasses.kiosk.controller;

import com.maumai.glasses.kiosk.entity.User;
import com.maumai.glasses.kiosk.entity.UserDto;
import com.maumai.glasses.kiosk.repository.UserRepository;
import com.maumai.glasses.kiosk.response.Response;
import com.maumai.glasses.kiosk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "이미지 저장 및 Flask 전송", description = "이미지를 저장하고 Flask 서버로 전송한다.")
    @PostMapping("/image/save/{userId}")
    public ResponseEntity<Response<String>> imageSave(
            @PathVariable Long userId,
            @RequestPart("image") MultipartFile[] files) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        ByteArrayResource byteArrayResource = new ByteArrayResource(files[0].getBytes()) {
            @Override
            public String getFilename() {
                return files[0].getOriginalFilename(); // 파일 이름 설정
            }
        };

        // Flask 서버 URL 설정
        String flaskUrl = "http://127.0.0.1:5000/upload";
        RestTemplate restTemplate = new RestTemplate();

        // 전송 데이터 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", byteArrayResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Flask 서버로 전송 및 응답 수신
        ResponseEntity<Map> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, Map.class);
        Map<String, Object> result = response.getBody();

        // 분석 결과를 user 엔티티에 저장
        if (result != null && result.containsKey("tone")) {
            String tone = (String) result.get("tone");
            user.setPersonalColor(tone);
            userRepository.save(user); // DB에 저장
        }

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