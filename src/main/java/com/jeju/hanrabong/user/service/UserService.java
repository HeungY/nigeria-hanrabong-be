package com.jeju.hanrabong.user.service;

import com.jeju.hanrabong.user.entity.User;
import com.jeju.hanrabong.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean checkDup(String nickname){   // 유저 중복 체크
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.isPresent();
    }

    public void createUser(String nickname) {
        User user = new User();
        user.setNickname(nickname);
        userRepository.save(user);
    }

    public List<User> top10(){  // 탑 10 반환
        return userRepository.findTop10();
    }

    public int getScore(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);

        return user.get().getScore();
    }
}
