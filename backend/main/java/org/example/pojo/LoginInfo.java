package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfo {
    private Long id;
    private String username;
    private String name;
    private String token;
    private String avatar;
    private Integer gender;
    private String phone;
    private String idCard;
    private BigDecimal balance;
}
