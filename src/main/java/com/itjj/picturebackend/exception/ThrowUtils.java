package com.itjj.picturebackend.exception;

/**
 * 为了方便抛出异常
 * 异常抛出工具类
 */
public class ThrowUtils {


    /**
     * 如果条件为true，抛出运行时异常
     * @param condition 条件
     * @param runtimeException 运行时异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }


    /**
     * 如果条件为true，抛出运行时异常
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
//        if (condition) {
//            throw new RuntimeException(errorCode.getMessage());
//        }
    }

    /**
     * 如果条件为true，抛出运行时异常
     * @param condition 条件
     * @param message 错误信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
//        if (condition) {
//            throw new RuntimeException(message);
//        }
    }

}
