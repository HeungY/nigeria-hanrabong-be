package com.jeju.hanrabong.user.service;

import com.jeju.hanrabong.user.entity.User;
import com.jeju.hanrabong.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    public void saveUser(String nickname, int score){    // 유저 저장
        User user = new User();
        user.setNickname(nickname);
        user.setScore(score);
        userRepository.save(user);
    }

    public boolean checkDup(String nickname){   // 유저 중복 체크
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.isPresent();
    }

    public List<User> top10(){  // 탑 10 반환
        return userRepository.findTop10();
    }

}
