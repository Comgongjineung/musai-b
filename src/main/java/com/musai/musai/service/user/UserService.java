package com.musai.musai.service.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.musai.musai.dto.user.SettingDTO;
import com.musai.musai.dto.user.UserDTO;
import com.musai.musai.entity.user.DefaultDifficulty;
import com.musai.musai.entity.user.Setting;
import com.musai.musai.entity.user.User;
import com.musai.musai.repository.user.SettingRepository;
import com.musai.musai.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;

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

    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        //업데이트 할 정보
        user.setNickname(userDTO.getNickname());
        user.setProfileImage(userDTO.getProfileImage());
        userRepository.save(user);

        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

    public SettingDTO getSettingById(Long userId) {
        Setting setting = settingRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        return SettingDTO.builder()
                .userId(setting.getUserId())
                .defaultDifiiculty(setting.getDefaultDiffiiculty())
                .allowCAlarm(setting.getAllowCalarm())
                .allowRAlarm(setting.getAllowRalarm())
                .build();
    }

    public SettingDTO updateLevel(Long userId, DefaultDifficulty level) {
        Setting setting = settingRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        //업데이트 정보
        setting.setDefaultDiffiiculty(level);
        settingRepository.save(setting);

        return SettingDTO.builder()
                .userId(setting.getUserId())
                .defaultDifiiculty(setting.getDefaultDiffiiculty())
                .allowCAlarm(setting.getAllowCalarm())
                .allowRAlarm(setting.getAllowRalarm())
                .build();
    }
}
