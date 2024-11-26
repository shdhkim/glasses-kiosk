package com.maumai.glasses.kiosk.service;

import com.maumai.glasses.kiosk.entity.Glasses;
import com.maumai.glasses.kiosk.entity.GlassesRecommend;
import com.maumai.glasses.kiosk.entity.User;
import com.maumai.glasses.kiosk.entity.UserDto;
import com.maumai.glasses.kiosk.repository.GlassesRecommendRepository;
import com.maumai.glasses.kiosk.repository.GlassesRepository;
import com.maumai.glasses.kiosk.repository.UserRepository;
import com.maumai.glasses.kiosk.response.Response;
import com.maumai.glasses.kiosk.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final GlassesRepository glassesRepository;
    private final GlassesRecommendRepository glassesRecommendRepository;

    @Autowired
    public UserService(UserRepository userRepository, RestTemplateBuilder restTemplateBuilder, GlassesRepository glassesRepository, GlassesRecommendRepository glassesRecommendRepository) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplateBuilder.build();
        this.glassesRepository = glassesRepository;
        this.glassesRecommendRepository = glassesRecommendRepository;
    }

    @Transactional
    public ResponseEntity<Response<String>> save(Long userId, MultipartFile[] files) throws IOException {
        User user;

        // ID로 사용자 조회. 없으면 새로운 User 객체 생성
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setUserId(userId);
        }

        // MultipartFile 이미지 저장
        if (files != null && files.length > 0 && !files[0].isEmpty()) {
            byte[] compressedImage = ImageUtils.compressImage(files[0].getBytes());
            user.setUserImage(compressedImage); // 요청받은 이미지 저장
        }

        // 먼저 User 객체를 데이터베이스에 저장
        User savedUser = userRepository.save(user);

        try {
            String flaskUrl = "http://127.0.0.1:5000/upload";

            // 파일을 ByteArrayResource로 변환
            ByteArrayResource byteArrayResource = new ByteArrayResource(files[0].getBytes()) {
                @Override
                public String getFilename() {
                    return files[0].getOriginalFilename(); // 파일 이름 설정
                }
            };

            // 전송 데이터 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", byteArrayResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Flask 서버로 전송 및 응답 수신
            ResponseEntity<Map> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> result = response.getBody();

            if (result != null) {
                // tone 값 저장
                if (result.containsKey("tone")) {
                    String tone = (String) result.get("tone");
                    user.setPersonalColor(tone);
                }

                // face_shape 값 저장
                if (result.containsKey("face_shape")) {
                    String faceShape = (String) result.get("face_shape");
                    user.setFaceShape(faceShape);
                }

                // glasses_id 값 저장 (최대 5개까지 받음)
                if (result.containsKey("glasses_id") && result.get("glasses_id") instanceof List) {
                    List<Long> glassesIds = (List<Long>) result.get("glasses_id");  // glassesId가 Long으로 넘어옴
                    if (glassesIds.size() > 5) {
                        glassesIds = glassesIds.subList(0, 5); // 최대 5개만 받기
                    }

                    // GlassesRecommend 엔티티로 변환하여 저장
                    List<GlassesRecommend> glassesRecommendList = new ArrayList<>();
                    for (Long glassesId : glassesIds) {
                        Optional<Glasses> glassesOptional = glassesRepository.findById(glassesId); // glassesId로 Glasses 엔티티 찾기
                        if (glassesOptional.isPresent()) {
                            Glasses glasses = glassesOptional.get();
                            GlassesRecommend glassesRecommend = GlassesRecommend.builder()
                                    .user(user)
                                    .glasses(glasses) // Glasses 엔티티 연결
                                    .build();

                            glassesRecommendList.add(glassesRecommend);
                        }
                    }

                    // GlassesRecommend 리스트를 User 객체에 설정
                    user.setGlassesRecommendList(glassesRecommendList);
                }

                userRepository.save(user); // 최종적으로 업데이트된 User 객체를 DB에 저장

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new Response<>("성공", "이미지 저장 및 Flask 서버 전송 성공", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Response<>("실패", "Flask 서버 응답 오류", null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("부분 성공", "Flask 서버 통신 실패. 데이터베이스에는 저장됨: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<Response<UserDto>> getUserDtoById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    // User 엔티티를 UserDto로 변환
                    UserDto userDto = new UserDto();
                    userDto.setGlassesFrame(user.getGlassesFrame());
                    userDto.setGlassesColor(user.getGlassesColor());
                    userDto.setFaceShape(user.getFaceShape());
                    userDto.setPersonalColor(user.getPersonalColor());

                    // 성공 응답
                    Response<UserDto> response = new Response<>("성공", "유저 정보 조회 성공", userDto);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    // 유저가 존재하지 않을 때 오류 응답
                    Response<UserDto> response = new Response<>("실패", "해당 ID의 사용자가 존재하지 않습니다.", null);
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                });
    }

    @Transactional
    public ResponseEntity<Response<String>> sendFeedbackToFlask(Long userId, String feedback) {
        // 사용자 확인 및 피드백 저장
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
        user.setFeedBack(feedback);
        userRepository.save(user); // DB에 피드백 업데이트

        // Flask 서버로 피드백 전송
        String flaskUrl = "http://localhost:5000/feedback";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("feedback", feedback);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Flask 서버와의 통신 (POST 요청)
            ResponseEntity<Map> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> result = response.getBody();

            // glasses_id 값 저장 (최대 5개까지 받음)
            if (result != null && result.containsKey("glasses_id") && result.get("glasses_id") instanceof List) {
                List<Long> glassesIds = (List<Long>) result.get("glasses_id");  // glassesId가 Long으로 넘어옴
                if (glassesIds.size() > 5) {
                    glassesIds = glassesIds.subList(0, 5); // 최대 5개만 받기
                }

                // 기존 추천 안경 삭제
                glassesRecommendRepository.deleteByUser(user);

                // 새로운 GlassesRecommend 엔티티 리스트 생성
                List<GlassesRecommend> glassesRecommendList = new ArrayList<>();
                for (Long glassesId : glassesIds) {
                    Optional<Glasses> glassesOptional = glassesRepository.findById(glassesId); // glassesId로 Glasses 엔티티 찾기
                    if (glassesOptional.isPresent()) {
                        Glasses glasses = glassesOptional.get();
                        GlassesRecommend glassesRecommend = GlassesRecommend.builder()
                                .user(user)
                                .glasses(glasses) // Glasses 엔티티 연결
                                .build();

                        glassesRecommendList.add(glassesRecommend);
                    }
                }

                // GlassesRecommend 리스트를 DB에 저장
                glassesRecommendRepository.saveAll(glassesRecommendList); // saveAll을 사용하여 여러 엔티티를 한 번에 저장
            } else {
                throw new IllegalArgumentException("Flask 서버에서 유효한 안경 데이터를 받지 못했습니다.");
            }

            // 성공 응답 반환
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>("성공", "피드백 저장 및 Flask 서버 전송 성공", null));
        } catch (Exception e) {
            // 오류 발생 시 적절한 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("부분 성공", "Flask 서버 통신 실패. 데이터베이스에는 저장됨. 오류: " + e.getMessage(), null));
        }
    }



    @Transactional
    public ResponseEntity<Response<User>> findUser(Long userId) {
        User user = findUserById(userId);
        return ResponseEntity.ok(new Response<>("true", "유저 조회 성공", user));
    }
    @Transactional
    public ResponseEntity<Response<String>> send(Long userId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

        try {
            // 이미지 압축 해제
            byte[] decompressedImage = ImageUtils.decompressImage(user.getUserImage());

            // Flask 서버로 이미지 전송
            String flaskUrl = "http://127.0.0.1:5000/process-image";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource byteArrayResource = new ByteArrayResource(decompressedImage) {
                @Override
                public String getFilename() {
                    return "user_image.jpg"; // 파일 이름 설정
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("image", byteArrayResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Flask 서버로 요청 전송 및 응답 수신
            ResponseEntity<Map> flaskResponse = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, Map.class);

            // Flask에서 받은 Base64 인코딩된 이미지
            Map<String, Object> responseBody = flaskResponse.getBody();
            if (responseBody != null && responseBody.containsKey("image")) {
                String base64EncodedImage = (String) responseBody.get("image");

                // DB 저장
                byte[] receivedImage = Base64.getDecoder().decode(base64EncodedImage);
                byte[] compressedMixImage = ImageUtils.compressImage(receivedImage);
                user.setMixImage(compressedMixImage);
                userRepository.save(user);

                // 성공 응답 반환 (Base64 인코딩된 이미지 포함)
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new Response<>("성공", "이미지를 성공적으로 Flask 서버에 전송하고 응답을 처리했습니다.", base64EncodedImage));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response<>("성공", "이미지를 성공적으로 Flask 서버에 전송하고 응답을 처리했습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("실패", "Flask 서버 통신 실패: " + e.getMessage(), null));
        }
    }
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
    }
}