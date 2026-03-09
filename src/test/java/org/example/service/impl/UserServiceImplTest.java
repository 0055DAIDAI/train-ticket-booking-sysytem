// UserServiceImplTest.java
package org.example.service.impl;

import org.example.BaseSpringBootTest;
import org.example.mapper.UserMapper;
import org.example.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest extends BaseSpringBootTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setName("Test User");
        testUser.setPhone("13800138000");
        testUser.setIdCard("110101199001011234");
        testUser.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void register_Success() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setName("New User");
        newUser.setPhone("13900139000");
        newUser.setIdCard("110101199001011235");

        when(userMapper.findByUsername("newuser")).thenReturn(null);
        when(userMapper.findByPhone("13900139000")).thenReturn(null);
        when(userMapper.findByIdCard("110101199001011235")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return 1;
        });

        // When
        User registeredUser = userService.register(newUser);

        // Then
        assertNotNull(registeredUser);
        assertEquals("newuser", registeredUser.getUsername());
        assertEquals(new BigDecimal("0.00"), registeredUser.getBalance());
        assertNotNull(registeredUser.getId());
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        // Given
        User newUser = new User();
        newUser.setUsername("duplicate");
        newUser.setPassword("password");
        newUser.setName("New User");
        newUser.setPhone("13900139000");
        newUser.setIdCard("110101199001011235");

        when(userMapper.findByUsername("duplicate")).thenReturn(testUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(newUser);
        });

        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void login_Success() {
        // Given
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // Mock password encoder behavior
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        testUser.setPassword(encoder.encode(rawPassword));

        // When
        var loginInfo = userService.login("testuser", rawPassword);

        // Then
        assertNotNull(loginInfo);
        assertEquals("testuser", loginInfo.getUsername());
        assertNotNull(loginInfo.getToken());
        assertTrue(loginInfo.getToken().length() > 0);
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        // Given
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("testuser", "wrongpassword");
        });

        assertEquals("密码错误", exception.getMessage());
    }

    @Test
    void updateLastLogin_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        when(userMapper.updateLastLogin(1L, now)).thenReturn(1);

        // When & Then
        assertDoesNotThrow(() -> {
            userService.updateLastLogin(1L, now);
        });

        verify(userMapper).updateLastLogin(1L, now);
    }
}
