package com.itjj.picturebackend.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 2L;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
