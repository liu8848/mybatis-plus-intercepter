package com.lqz.controller;

import com.lqz.model.User;
import com.lqz.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Tag(name = "用户 Apis")
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/save")
    @Operation(summary = "保存")
    public User save(@RequestBody User user) {
        user.setCreateTime(new Date());
        userService.save(user);
        return user;
    }
}
