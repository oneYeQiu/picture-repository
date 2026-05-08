package com.itjj.picturebackend.controller;

import com.itjj.picturebackend.common.BaseResponse;
import com.itjj.picturebackend.common.ResultUtils;
import com.itjj.picturebackend.exception.BusinessException;
import com.itjj.picturebackend.exception.ErrorCode;
import com.itjj.picturebackend.exception.ThrowUtils;
import com.itjj.picturebackend.model.dto.UserLoginRequest;
import com.itjj.picturebackend.model.dto.UserRegisterRequest;
import com.itjj.picturebackend.model.entity.User;
import com.itjj.picturebackend.model.vo.UserLoginVo;
import com.itjj.picturebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;


    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest registerRequest) {
//        if(registerRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
//        }
        ThrowUtils.throwIf(registerRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        long result = userService.register(registerRequest);
        return ResultUtils.success(result);
    }


    @GetMapping("/login")
    public BaseResponse<UserLoginVo> login(@RequestBody UserLoginRequest loginRequest) {
        UserLoginVo user = userService.login(loginRequest);
        return ResultUtils.success(user);
    }
}
