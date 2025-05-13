package com.musai.musai;

import com.musai.musai.entity.User;
import com.musai.musai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        // 테스트 전 데이터 클린업 (기존 데이터 삭제)
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser() {
        // 유저 생성
        User user = new User("babo", "1234", "그만하자");
        User savedUser = userRepository.save(user);

        // 저장된 유저가 null이 아님을 확인
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId()); // id가 자동으로 생성되므로 확인

        // 저장된 유저의 이름이 올바른지 확인
        assertEquals("babo", savedUser.getId());
        assertEquals("그만하자", savedUser.getNickname());
    }
}
