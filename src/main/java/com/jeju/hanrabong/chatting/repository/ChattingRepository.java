package com.jeju.hanrabong.chatting.repository;

import com.jeju.hanrabong.chatting.model.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Integer> {
    List<Chatting> findTop1ByOrderByTimestampDesc();
}
