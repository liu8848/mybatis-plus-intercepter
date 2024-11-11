package com.lqz.dao;

import com.lqz.injector.mapper.CustomMapper;
import com.lqz.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends CustomMapper<User> {
}
