package com.musai.musai.repository.user;

import com.musai.musai.entity.user.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    Optional<Preference> findByUserId(Long userId);
}
