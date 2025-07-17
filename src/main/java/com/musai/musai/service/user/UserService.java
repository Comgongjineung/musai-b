package com.musai.musai.service.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.musai.musai.entity.user.User;
import com.musai.musai.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findOrCreateUserFromGoogle(GoogleIdToken.Payload payload) {
        String oauthProvider = "google";
        String oauthId = payload.getSubject();
        String email = payload.getEmail();
        String nickname = (String) payload.get("name");
        String profileImage = (String) payload.get("picture");

        return userRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .oauthProvider(oauthProvider)
                        .oauthId(oauthId)
                        .email(email)
                        .nickname(nickname)
                        .profileImage(profileImage)
                        .build()));
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
