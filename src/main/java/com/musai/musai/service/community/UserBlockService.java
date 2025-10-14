package com.musai.musai.service.community;

import com.musai.musai.dto.community.UserBlockRequest;
import com.musai.musai.dto.community.UserBlockResponse;
import com.musai.musai.entity.community.UserBlock;
import com.musai.musai.entity.user.User;
import com.musai.musai.repository.community.UserBlockRepository;
import com.musai.musai.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final UserBlockRepository userBlockRepository;
    private final UserRepository userRepository;

    public Long getUserIdByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자(" + userEmail + ")를 찾을 수 없습니다."));
        return user.getUserId();
    }

    @Transactional
    public UserBlockResponse blockUserByEmail(String userEmail, UserBlockRequest request) {
        User blocker = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자(" + userEmail + ")를 찾을 수 없습니다."));

        Long blockerId = blocker.getUserId();

        return blockUser(blockerId, request);
    }

    @Transactional
    public void deleteBlockByEmail(String userEmail, Long blockedUserId) {
        User blocker = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자(" + userEmail + ")를 찾을 수 없습니다."));

        Long blockerId = blocker.getUserId();

        deleteBlock(blockerId, blockedUserId);
    }

    public List<Long> getBlockedUsersByEmail(String userEmail) {
        User blocker = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자(" + userEmail + ")를 찾을 수 없습니다."));

        Long blockerId = blocker.getUserId();

        return userBlockRepository.findAllByBlockerId(blockerId)
                .stream()
                .map(UserBlock::getBlockedUserId)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserBlockResponse blockUser(Long blockerId, UserBlockRequest request) {
        Long blockedUserId = request.getBlockedUserId();

        if (blockerId.equals(blockedUserId)) {
            throw new IllegalStateException("자신을 차단할 수 없습니다.");
        }

        userBlockRepository.findByBlockerIdAndBlockedUserId(blockerId, blockedUserId)
                .ifPresent(b -> {
                    throw new IllegalStateException("이미 차단한 사용자입니다.");
                });

        UserBlock newBlock = UserBlock.builder()
                .blockerId(blockerId)
                .blockedUserId(blockedUserId)
                .createdAt(LocalDateTime.now())
                .build();

        UserBlock savedBlock = userBlockRepository.save(newBlock);

        return new UserBlockResponse(savedBlock.getBlockId(), savedBlock.getBlockedUserId());
    }

    @Transactional
    public void deleteBlock(Long blockerId, Long blockedUserId) {
        UserBlock blockToDelete = userBlockRepository
                .findByBlockerIdAndBlockedUserId(blockerId, blockedUserId)
                .orElseThrow(() -> new IllegalStateException("해당 사용자에 대한 차단 기록이 존재하지 않습니다."));

        userBlockRepository.delete(blockToDelete);
    }
}
