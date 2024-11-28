package com.maumai.glasses.kiosk.repository;

import com.maumai.glasses.kiosk.entity.GlassesRecommend;
import com.maumai.glasses.kiosk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GlassesRecommendRepository extends JpaRepository<GlassesRecommend, Long> {
    List<GlassesRecommend> findByUser(User user);// 특정 사용자에 대한 안경 추천 리스트

    void deleteByUser(User user); // 사용자로 추천 안경을 삭제하는 메서드

    @Query("SELECT gr FROM GlassesRecommend gr WHERE gr.user = :user ORDER BY gr.id DESC")
    Optional<GlassesRecommend> findTopByUserOrderByIdDesc(@Param("user") User user);
}