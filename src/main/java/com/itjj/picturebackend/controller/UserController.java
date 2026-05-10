package com.itjj.picturebackend.controller;

import com.itjj.picturebackend.annotation.AuthCheck;
import com.itjj.picturebackend.common.BaseResponse;
import com.itjj.picturebackend.common.ResultUtils;
import com.itjj.picturebackend.constant.UserConstant;
import com.itjj.picturebackend.exception.ErrorCode;
import com.itjj.picturebackend.exception.ThrowUtils;
import com.itjj.picturebackend.model.dto.user.UserLoginRequest;
import com.itjj.picturebackend.model.dto.user.UserRegisterRequest;
import com.itjj.picturebackend.model.entity.User;
import com.itjj.picturebackend.model.vo.UserLoginVO;
import com.itjj.picturebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;


    /**
     * 用户注册
     *
     * @param registerRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest registerRequest) {
//        if(registerRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
//        }
        ThrowUtils.throwIf(registerRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        long result = userService.register(registerRequest);
        return ResultUtils.success(result);
    }


    /**
     * 用户登录
     *
     * @param userloginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> login(@RequestBody UserLoginRequest userloginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userloginRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        UserLoginVO user = userService.login(userloginRequest, request);
        return ResultUtils.success(user);
    }

    /**
     * 获取登录用户
     *
     * @param request
     * @return
     */
    @PostMapping("/get/login")
    public BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);

        return ResultUtils.success(userService.getLoginUserVo(user));
    }


    /**
     * 用户注销 （ 清除登录状态） （ 退出登录）
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }
}
