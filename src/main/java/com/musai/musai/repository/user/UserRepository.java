package com.musai.musai.repository.user;

import com.musai.musai.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // 필요한 쿼리 메서드 추가 가능
    User save(User user);

    User findByNickname(String nickname);
}
