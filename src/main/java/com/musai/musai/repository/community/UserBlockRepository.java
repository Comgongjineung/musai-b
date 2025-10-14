package com.musai.musai.repository.community;

import com.musai.musai.entity.community.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    Optional<UserBlock> findByBlockerIdAndBlockedUserId(Long blockerId, Long blockedUserId);

    void deleteByBlockerIdAndBlockedUserId(Long blockerId, Long blockedUserId);

    List<UserBlock> findAllByBlockerId(Long blockerId);

}