package com.itjj.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itjj.picturebackend.model.dto.user.UserLoginRequest;
import com.itjj.picturebackend.model.dto.user.UserQueryRequest;
import com.itjj.picturebackend.model.dto.user.UserRegisterRequest;
import com.itjj.picturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itjj.picturebackend.model.vo.UserLoginVO;
import com.itjj.picturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 获取登录用户 ， 仅供内部使用
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取用户登录信息
     *
     * @param user
     * @return
     */
    UserLoginVO getLoginUserVo(User user);

    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取用户查询条件的QueryWrapper对象  (UserQueryRequest查询对象里面的字段多 如果都写在查询方法里面 会导致查询效率低， 所以这里使用QueryWrapper)
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户登录
     * @param userloginRequest
     * @return
     */
    UserLoginVO login(UserLoginRequest userloginRequest, HttpServletRequest request);


    /**
     * 用户注销 （ 清除登录状态） （ 退出登录）
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);
}
