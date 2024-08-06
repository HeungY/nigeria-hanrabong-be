package com.jeju.hanrabong.user.repository;

import com.jeju.hanrabong.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u ORDER BY u.score DESC")
    List<User> findTop10();
}
