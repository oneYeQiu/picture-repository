package com.itjj.picturebackend.common;

import com.itjj.picturebackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应类
 * @param <T> 响应数据类型
 * 为了方便前端处理，统一响应格式 ， 并实现Serializable接口 ， 用于序列化
 * @author theonefx
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(int code, T data) {
        this(code, "", data);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage(), null);
    }
}
