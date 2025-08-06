package com.musai.musai.repository.alarm;

import com.musai.musai.entity.alarm.Alarm;
import com.musai.musai.entity.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Alarm> findByUserIdAndIsReadFalse(Long userId);
    Long countByUserIdAndIsReadFalse(Long userId);
}
