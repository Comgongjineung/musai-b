package com.musai.musai.service;

import com.musai.musai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void testConnection() {
        // 예시: 저장된 모든 유저 출력
        userRepository.findAll().forEach(user -> System.out.println(user.getNickname()));
    }
}
