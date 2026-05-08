package com.itjj.picturebackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itjj.picturebackend.exception.BusinessException;
import com.itjj.picturebackend.exception.ErrorCode;
import com.itjj.picturebackend.exception.ThrowUtils;
import com.itjj.picturebackend.model.dto.UserLoginRequest;
import com.itjj.picturebackend.model.dto.UserRegisterRequest;
import com.itjj.picturebackend.model.entity.User;
import com.itjj.picturebackend.model.enums.UserRoleEnum;
import com.itjj.picturebackend.model.vo.UserLoginVo;
import com.itjj.picturebackend.service.UserService;
import com.itjj.picturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author 36664
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-05-08 20:09:42
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 用户注册
     * @param request
     * @return
     */
    @Override
    public long register(UserRegisterRequest request) {
        // 1. 校验参数
        if(StrUtil.hasBlank(request.getUserAccount(), request.getUserPassword(), request.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        ThrowUtils.throwIf(request.getUserAccount().length() < 4, ErrorCode.PARAMS_ERROR, "账号账号长度不能小于4位");

        ThrowUtils.throwIf(request.getUserPassword().length() < 6, ErrorCode.PARAMS_ERROR, "密码长度不能小于6位");

        ThrowUtils.throwIf(!request.getCheckPassword().equals(request.getUserPassword()), ErrorCode.PARAMS_ERROR, "两次密码输入不一致");

        // 2. 校验用户是否存在
        ThrowUtils.throwIf(this.baseMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, request.getUserAccount())) != null, ErrorCode.PARAMS_ERROR, "账号已存在");
        // 3. 密码加密
        request.setUserPassword(getEncryptPassword(request.getUserPassword()));

        // 4. 保存用户,插入数据库
        User user = new User();
        user.setUserAccount(request.getUserAccount());
        user.setUserPassword(request.getUserPassword());
        user.setUserName("默认用户");
        user.setUserRole(UserRoleEnum.USER.getValue());

        int insert = baseMapper.insert(user);
        ThrowUtils.throwIf(insert != 1, ErrorCode.SYSTEM_ERROR, "注册失败, 数据库操作失败");

        return user.getId();
    }

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 加盐， 混淆密码
        final String SALT = "itjj";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());

    }

    @Override
    public UserLoginVo login(UserLoginRequest loginRequest) {
        // 1. 校验参数
        if(StrUtil.hasBlank(loginRequest.getUsername(), loginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        // 2. 校验用户是否存在
        User user = this.baseMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, loginRequest.getUsername()));
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号不存在");

        UserLoginVo userLoginVo = UserLoginVo.builder()
                .userId(user.getId())
                .username(user.getUserName())
                .role(UserRoleEnum.valueOf(user.getUserRole()))
                .profile(user.getUserProfile())
                .avatar(user.getUserAvatar())
                .build();
        return userLoginVo;
    }

}




