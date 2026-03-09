package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Data           // 自动生成 getter、setter、toString、equals、hashCode
@NoArgsConstructor  // 无参构造函数
@AllArgsConstructor // 全参构造函数
@Builder        // 支持链式创建对象
public class User {
        private Long id;
        private String username;
        private String password;
        private String name;
        private String idCard;
        private String phone;
        private BigDecimal balance;
        private String avatar;
        private Integer gender; // 0=未知,1=男,2=女
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private LocalDateTime lastLogin;
        private Integer status; // 0=禁用,1=正常,2=锁定
    }

