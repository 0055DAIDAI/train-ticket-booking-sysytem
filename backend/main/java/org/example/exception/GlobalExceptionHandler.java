// 文件路径：org/example/exception/GlobalExceptionHandler.java
package org.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 注意：异常处理方法的顺序很重要，具体异常在前，Exception 在后
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理唯一键冲突异常（如用户名、手机号重复）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据库唯一键冲突: {}", e.getMessage());
        String field = extractDuplicateKeyField(e.getMessage());
        return Result.error(field + "已存在");
    }

    /**
     * 处理业务异常（建议定义 BusinessException）
     */
    // @ExceptionHandler(BusinessException.class)
    // public Result<?> handleBusinessException(BusinessException e) {
    //     log.warn("业务异常: {}", e.getMessage());
    //     return Result.error(e.getMessage());
    // }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        return Result.error("请求参数不合法");
    }

    /**
     * 兜底：处理所有未被捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统发生未预期异常", e); // 使用 error 级别
        return Result.error("服务器内部错误");
    }

    /**
     * 从 DuplicateKeyException 消息中提取冲突字段名
     */
    private String extractDuplicateKeyField(String msg) {
        if (msg == null) return "记录";

        // 尝试匹配 MySQL 常见格式：... for key 'username'
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("for key '?[^']*(?:\\.|`)?([a-zA-Z_]+)'?");
        java.util.regex.Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            String field = matcher.group(1);
            return switch (field) {
                case "username" -> "用户名";
                case "phone" -> "手机号";
                case "id_card" -> "身份证号";
                case "email" -> "邮箱";
                default -> field;
            };
        }
        return "记录"; // 默认提示
    }
}