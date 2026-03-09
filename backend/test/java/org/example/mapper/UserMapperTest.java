package org.example.mapper;

import org.example.BaseSpringBootTest;
import org.example.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserMapperTest extends BaseSpringBootTest {

    @Autowired
    private UserMapper userMapper;


    @Test
    void findByUsername() {
        // Given
        User user = new User();
        user.setUsername("unique_username");
        user.setPassword("password");
        user.setName("Test User");
        user.setPhone("13800138000");
        user.setIdCard("110101199001011235"); // 修改为不同的身份证号
        user.setBalance(new BigDecimal("100.00"));
        user.setGender(1);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        // When
        User foundUser = userMapper.findByUsername("unique_username");

        // Then
        assertNotNull(foundUser);
        assertEquals("unique_username", foundUser.getUsername());
    }
}
