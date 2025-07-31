package com.musai.musai.repository.alarm;

import com.musai.musai.entity.alarm.Alarm;
import com.musai.musai.entity.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
