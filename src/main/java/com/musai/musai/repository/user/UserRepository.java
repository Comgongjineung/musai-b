package com.musai.musai.repository.user;

import com.musai.musai.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthId(String oauthId);
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    Optional<User> findByNickname(String nickname);
    Optional<User> findByNicknameAndUserIdNot(String nickname, Long userId);
    boolean existsByNickname(String nickname);
    boolean existsByNicknameAndUserIdNot(String nickname, Long userId);
}
