package com.jeju.hanrabong.chatting.controller;

import com.jeju.hanrabong.chatting.model.Chatting;
import com.jeju.hanrabong.chatting.repository.ChattingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChattingController {

    private final ChattingRepository chattingRepository;

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChattingController(ChattingRepository chattingRepository) {
        this.chattingRepository = chattingRepository;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Chatting sendMessage(Chatting chatting) {
        chatting.setTimestamp(LocalDateTime.now());
        chattingRepository.save(chatting);
        return chatting;
    }

    @GetMapping("/latest-messages")
    @ResponseBody
    public List<Chatting> getLatestMessages() {
        return chattingRepository.findTop1ByOrderByTimestampDesc();
    }

}
