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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;

    @Transactional
    public User findOrCreateUserFromGoogle(GoogleIdToken.Payload payload) {
        String oauthProvider = "google";
        String oauthId = payload.getSubject();
        String email = payload.getEmail();
        String nickname = (String) payload.get("name");
        String profileImage = (String) payload.get("picture");

        return userRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
            .orElseGet(() -> {
                User user = userRepository.save(User.builder()
                        .oauthProvider(oauthProvider)
                        .oauthId(oauthId)
                        .email(email)
                        .nickname(nickname)
                        .profileImage(profileImage)
                        .build());
                settingRepository.save(Setting.builder()
                        .userId(user.getUserId())
                        .defaultDiffiiculty(DefaultDifficulty.NORMAL)
                        .allowCalarm(true)
                        .allowRalarm(true)
                        .build());
                return user;
            });
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

    public void checkNickname(Long userId, String nickname) {
        if (userId == null) {
            // 회원가입 시: 전체에서 닉네임 중복 확인
            if (userRepository.existsByNickname(nickname)) {
                throw new RuntimeException("이미 사용 중인 닉네임입니다.");
            }
        } else {
            if (!userRepository.existsById(userId)) {
                throw new RuntimeException("해당 유저가 없습니다.");
            }
            
            if (userRepository.existsByNicknameAndUserIdNot(nickname, userId)) {
                throw new RuntimeException("이미 사용 중인 닉네임입니다.");
            }
        }
    }

    public UserDTO updateUser(UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        if (userDTO.getNickname() != null && !userDTO.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNicknameAndUserIdNot(userDTO.getNickname(), userId)) {
                throw new RuntimeException("이미 사용 중인 닉네임입니다.");
            }
        }

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
