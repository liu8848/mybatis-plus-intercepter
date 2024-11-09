package com.lqz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lqz.dao.UserMapper;
import com.lqz.model.User;
import com.lqz.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
