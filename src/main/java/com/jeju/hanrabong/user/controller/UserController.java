package com.jeju.hanrabong.user.controller;

import com.jeju.hanrabong.user.entity.User;
import com.jeju.hanrabong.user.entity.UserDTO;
import com.jeju.hanrabong.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/top10")
    public ResponseEntity<List<User>> top10() {
        return ResponseEntity.ok(userService.top10());
    }

    @PostMapping("/save")
    public void saveUser(@RequestBody UserDTO userDTO) {
        if (!userService.checkDup(userDTO.getNickname())) {
            userService.saveUser(userDTO.getNickname(), userDTO.getScore());
        }
    }
}
