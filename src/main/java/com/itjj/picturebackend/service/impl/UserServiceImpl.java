package com.itjj.picturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itjj.picturebackend.constant.UserConstant;
import com.itjj.picturebackend.exception.BusinessException;
import com.itjj.picturebackend.exception.ErrorCode;
import com.itjj.picturebackend.exception.ThrowUtils;
import com.itjj.picturebackend.model.dto.user.UserLoginRequest;
import com.itjj.picturebackend.model.dto.user.UserQueryRequest;
import com.itjj.picturebackend.model.dto.user.UserRegisterRequest;
import com.itjj.picturebackend.model.entity.User;
import com.itjj.picturebackend.model.enums.UserRoleEnum;
import com.itjj.picturebackend.model.vo.UserLoginVO;
import com.itjj.picturebackend.model.vo.UserVO;
import com.itjj.picturebackend.service.UserService;
import com.itjj.picturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 36664
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-05-08 20:09:42
*/
@Service
@Slf4j
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

//        this.baseMapper.insert(user);
//        ThrowUtils.throwIf(user.getId() == null, ErrorCode.SYSTEM_ERROR, "注册失败, 数据库操作失败");

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

    /**
     * 获取脱敏后的登录用户信息
     *
     * @param user
     * @return
     */
    @Override
    public UserLoginVO getLoginUserVo(User user) {
        // 1. 校验用户是否存在
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 2. 脱敏处理
        UserLoginVO userLoginVo = new UserLoginVO();
        BeanUtils.copyProperties(user, userLoginVo);
        return userLoginVo;
    }


    /**
     * 获取登录用户信息 ， 仅供内部使用
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 判断用户是否登录
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(user == null || user.getId() == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 从数据库里查数据 更新用户信息 （ 追求性能可以用缓存 不去数据库里查 ）
        user = this.baseMapper.selectById(user.getId());
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        return user;
    }

    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        // 1. 校验用户是否存在
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 2. 脱敏处理
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏后的用户信息列表
     *
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 构建查询条件
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 1. 校验参数
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "参数不能为空");
        // 2. 构建查询条件
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField),sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }

    /**
     * 用户登录
     *
     * @param userloginRequest
     * @return 用户脱敏信息
     */
    @Override
    public UserLoginVO login(UserLoginRequest userloginRequest, HttpServletRequest request) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userloginRequest.getUsername(), userloginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        // 2. 对密码进行加密
        userloginRequest.setPassword(getEncryptPassword(userloginRequest.getPassword()));
        // 3. 判断用户是否存在
        User user = this.baseMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, userloginRequest.getUsername())
                .eq(User::getUserPassword, userloginRequest.getPassword()));
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号不存在或密码错误");
        // 4. 保存用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        // 5. 脱敏处理
        UserLoginVO userLoginVo = getLoginUserVo(user);

        return userLoginVo;
    }

    /**
     * 用户注销 （ 清除登录状态） （ 退出登录）
     *
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 判断用户是否登录
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(user == null, ErrorCode.OPERATION_ERROR, "用户未登录");
        // 清除登录状态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }
        return UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

}




