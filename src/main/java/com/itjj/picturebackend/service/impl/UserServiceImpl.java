package com.itjj.picturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itjj.picturebackend.model.entity.User;
import com.itjj.picturebackend.service.UserService;
import com.itjj.picturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 36664
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-05-08 20:09:42
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




