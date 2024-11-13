package com.lqz.controller;

import com.lqz.dao.UserMapper;
import com.lqz.model.User;
import com.lqz.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Api(tags = "用户相关 Apis")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/save")
    @ApiOperation(value = "保存")
    public User save(@RequestBody User user) {
        userService.save(user);
        return user;
    }

    @PostMapping("/saveBatch")
    public String saveBath(@RequestBody List<User> users) {
        userMapper.saveBatchsss(users,1000);
        return "success";
    }

    @PostMapping("/insertOrUpdateBatch")
    public String insertOrUpdateBatch(@RequestBody List<User> users) {
        userMapper.saveOrUpdateBatchByUK(users,1000);
        return "success";
    }
}
