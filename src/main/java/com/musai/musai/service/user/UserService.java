package com.musai.musai.service.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.dto.user.SettingDTO;
import com.musai.musai.dto.user.UserDTO;
import com.musai.musai.entity.community.Post;
import com.musai.musai.entity.user.DefaultDifficulty;
import com.musai.musai.entity.user.Setting;
import com.musai.musai.entity.user.User;
import com.musai.musai.repository.community.CommentRepository;
import com.musai.musai.repository.community.PostRepository;
import com.musai.musai.repository.user.SettingRepository;
import com.musai.musai.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.musai.musai.entity.community.Comment;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

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

        return toUserDTO(user);
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

        return toUserDTO(user);
    }

    public UserDTO deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        userRepository.delete(user);
        return toUserDTO(user);
    }

    public SettingDTO getSettingById(Long userId) {
        Setting setting = settingRepository.findById(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
                    
                    Setting newSetting = Setting.builder()
                            .userId(userId)
                            .defaultDiffiiculty(DefaultDifficulty.NORMAL)
                            .allowCalarm(true)
                            .allowRalarm(true)
                            .build();
                    return settingRepository.save(newSetting);
                });

        return toSettingDTO(setting);
    }

    public SettingDTO updateLevel(Long userId, DefaultDifficulty level) {
        Setting setting = settingRepository.findById(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
                    
                    Setting newSetting = Setting.builder()
                            .userId(userId)
                            .defaultDiffiiculty(level)
                            .allowCalarm(true)
                            .allowRalarm(true)
                            .build();
                    return settingRepository.save(newSetting);
                });

        //업데이트 정보
        setting.setDefaultDiffiiculty(level);
        settingRepository.save(setting);

        return toSettingDTO(setting);
    }

    public SettingDTO updateRecogAlarm(Long userId) {
        Setting updateSet = settingRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Boolean recogAlarm = updateSet.getAllowRalarm();
        if (recogAlarm)
            recogAlarm = false;
        else
            recogAlarm = true;

        updateSet.setAllowRalarm(recogAlarm);
        settingRepository.save(updateSet);

        return toSettingDTO(updateSet);
    }

    public SettingDTO updateCommunityAlarm(Long userId) {
        Setting updateSet = settingRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Boolean communityAlarm = updateSet.getAllowCalarm();
        if (communityAlarm)
            communityAlarm = false;
        else
            communityAlarm = true;

        updateSet.setAllowCalarm(communityAlarm);
        settingRepository.save(updateSet);

        return toSettingDTO(updateSet);
    }

    public List<PostDTO> myPost(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(PostDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PostDTO> myComment(Long userId) {
        List<Long> postIds = commentRepository.findByUserId(userId)
                .stream()
                .map(Comment::getPostId)
                .distinct()
                .collect(Collectors.toList());

        List<Post> posts = postRepository.findAllById(postIds);
        return posts.stream()
                .map(PostDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

    private SettingDTO toSettingDTO(Setting setting) {
        return SettingDTO.builder()
                .userId(setting.getUserId())
                .defaultDifficulty(setting.getDefaultDiffiiculty())
                .allowRAlarm(setting.getAllowRalarm())
                .allowCAlarm(setting.getAllowCalarm())
                .build();
    }
}
