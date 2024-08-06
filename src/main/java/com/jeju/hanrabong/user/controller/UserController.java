package com.jeju.hanrabong.user.controller;

import com.jeju.hanrabong.response.Response;
import com.jeju.hanrabong.user.entity.User;
import com.jeju.hanrabong.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Response<String>> createUser(@RequestParam String nickname) {
        if (!userService.checkDup(nickname)) {
            userService.createUser(nickname);
            return Response.createDataResponse("User created successfully");
        } else {
            return Response.createDataResponse("User duplicated");
        }
    }

    @GetMapping("/score")
    public ResponseEntity<Response<Integer>> getUsers(@RequestParam String nickname) {
        int score = userService.getScore(nickname);
        return Response.createDataResponse(score);
    }

    @GetMapping("/ranking")
    public ResponseEntity<Response<List<User>>> getTop10Users() {
        List<User> users = userService.top10();
        return Response.createDataResponse(users);
    }




}
