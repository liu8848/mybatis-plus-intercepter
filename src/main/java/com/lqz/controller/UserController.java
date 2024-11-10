package com.lqz.controller;

import com.lqz.model.User;
import com.lqz.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Api(tags = "用户相关 Apis")
public class UserController {

    private final UserService userService;

    @PostMapping("/save")
    @ApiOperation(value = "保存")
    public User save(@RequestBody User user) {
        userService.save(user);
        return user;
    }
}
