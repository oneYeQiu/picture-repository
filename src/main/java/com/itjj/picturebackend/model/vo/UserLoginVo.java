package com.itjj.picturebackend.model.vo;

import java.io.Serializable;

import com.itjj.picturebackend.model.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVo implements Serializable {
    private static final long serialVersionUID = 3L;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 角色
     */
    private UserRoleEnum role;
    /**
     * 用户简介
     */
    private String profile;
    /**
     * 用户头像
     */
    private String avatar;

}
