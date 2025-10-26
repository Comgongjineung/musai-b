package com.musai.musai.service.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.musai.musai.dto.community.PostDTO;
import com.musai.musai.dto.user.SettingDTO;
import com.musai.musai.dto.user.UserDTO;
import com.musai.musai.entity.community.Post;
import com.musai.musai.entity.user.DefaultDifficulty;
import com.musai.musai.entity.preference.Preference;
import com.musai.musai.entity.user.Setting;
import com.musai.musai.entity.user.User;
import com.musai.musai.repository.alarm.AlarmRepository;
import com.musai.musai.repository.alarm.TokenRepository;
import com.musai.musai.repository.bookmark.BookmarkRepository;
import com.musai.musai.repository.community.CommentRepository;
import com.musai.musai.repository.community.LikeRepository;
import com.musai.musai.repository.community.PostReportRepository;
import com.musai.musai.repository.community.PostRepository;
import com.musai.musai.repository.community.UserBlockRepository;
import com.musai.musai.repository.community.UserReportRepository;
import com.musai.musai.repository.preference.PreferenceRepository;
import com.musai.musai.repository.ticket.TicketRepository;
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
    private final PreferenceRepository preferenceRepository;
    private final LikeRepository likeRepository;
    private final UserBlockRepository userBlockRepository;
    private final UserReportRepository userReportRepository;
    private final PostReportRepository postReportRepository;
    private final TicketRepository ticketRepository;
    private final BookmarkRepository bookmarkRepository;
    private final AlarmRepository alarmRepository;
    private final TokenRepository tokenRepository;

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
                String defaultPreferences = createDefaultPreferencesJson();
                preferenceRepository.save(Preference.builder()
                        .userId(user.getUserId())
                        .preferences(defaultPreferences)
                        .build());
                
                return user;
            });
    }

    @Transactional
    public User findOrCreateUserFromApple(String email, String sub, String nickname, String profileImage) {
        String oauthProvider = "apple";
        String oauthId = sub;

        return userRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
                .orElseGet(() -> {
                    User user = userRepository.save(User.builder()
                            .oauthProvider(oauthProvider)
                            .oauthId(oauthId)
                            .email(email)
                            .nickname(nickname != null ? nickname : "AppleUser")
                            .profileImage(profileImage)
                            .build());

                    settingRepository.save(Setting.builder()
                            .userId(user.getUserId())
                            .defaultDiffiiculty(DefaultDifficulty.NORMAL)
                            .allowCalarm(true)
                            .allowRalarm(true)
                            .build());

                    String defaultPreferences = createDefaultPreferencesJson();
                    preferenceRepository.save(Preference.builder()
                            .userId(user.getUserId())
                            .preferences(defaultPreferences)
                            .build());

                    return user;
                });
    }

    private String createDefaultPreferencesJson() {
        return "{\"고대 미술\":0,\"중세 미술\":0,\"르네상스\":0,\"바로크\":0,\"로코코\":0,\"신고전주의\":0,\"낭만주의\":0,\"사실주의\":0,\"인상주의\":0,\"후기 인상주의\":0,\"아르누보\":0,\"야수파 & 표현주의\":0,\"입체주의\":0,\"미래주의 & 구성주의\":0,\"다다 & 초현실주의\":0,\"추상표현주의\":0,\"팝아트\":0,\"미니멀리즘 & 현대미술\":0,\"동아시아\":0,\"동남아시아\":0,\"남아시아\":0,\"중앙아시아\":0,\"서아시아 / 중동\":0}";
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

    @Transactional
    public UserDTO deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        List<Post> userPosts = postRepository.findByUserId(userId);
        for (Post post : userPosts) {
            likeRepository.deleteByPostId(post.getPostId());
            commentRepository.deleteByPostId(post.getPostId());
        }
        postRepository.deleteAll(userPosts);

        List<Comment> userComments = commentRepository.findByUserId(userId);
        commentRepository.deleteAll(userComments);

        likeRepository.deleteByUserId(userId);

        userBlockRepository.deleteByBlockerIdOrBlockedUserId(userId, userId);
        userReportRepository.deleteByReporterIdOrReportedUserId(userId, userId);
        postReportRepository.deleteByReporterId(userId);

        ticketRepository.deleteByUserId(userId);
        List<com.musai.musai.entity.bookmark.Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        bookmarkRepository.deleteAll(bookmarks);

        alarmRepository.deleteByUserId(userId);

        Preference preference = preferenceRepository.findById(userId).orElse(null);
        if (preference != null) {
            preferenceRepository.delete(preference);
        }

        Setting setting = settingRepository.findById(userId).orElse(null);
        if (setting != null) {
            settingRepository.delete(setting);
        }

        tokenRepository.findById(userId).ifPresent(tokenRepository::delete);

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