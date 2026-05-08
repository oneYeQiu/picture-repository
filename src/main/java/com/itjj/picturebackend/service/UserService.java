package com.itjj.picturebackend.service;

import com.itjj.picturebackend.model.dto.UserLoginRequest;
import com.itjj.picturebackend.model.dto.UserRegisterRequest;
import com.itjj.picturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itjj.picturebackend.model.vo.UserLoginVo;

/**
* @author 36664
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-05-08 20:09:42
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param request
     * @return
     */
    long register(UserRegisterRequest request);

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     * @param loginRequest
     * @return
     */
    UserLoginVo login(UserLoginRequest loginRequest);
}
