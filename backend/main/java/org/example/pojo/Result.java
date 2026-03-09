// 文件路径：org/example/pojo/Result.java
package org.example.pojo;

import lombok.Data;

/**
 * 通用返回结果类，用于封装接口响应数据
 * @param <T> 返回数据的类型
 */
@Data
public class Result<T> {

    public static final Integer SUCCESS = 1;
    public static final Integer ERROR = 0;

    private Integer code;
    private String msg;
    private T data;

    public Result() {}

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    /**
     * 成功，无数据
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS, "成功", null);
    }

    /**
     * 成功，带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS, "成功", data);
    }

    /**
     * 失败，带错误信息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ERROR, message, null);
    }

    /**
     * 自定义状态码和消息（可选）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}