package com.maumai.glasses.kiosk.repository;

import com.maumai.glasses.kiosk.entity.Glasses;
import com.maumai.glasses.kiosk.entity.GlassesRecommend;
import com.maumai.glasses.kiosk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GlassesRepository extends JpaRepository<GlassesRecommend, Long> {
    List<GlassesRecommend> findByUser(User user);
}