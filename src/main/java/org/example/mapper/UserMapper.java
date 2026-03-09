package org.example.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.User;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    User findByPhone(@Param("phone") String phone);

    /**
     * 根据身份证号查询用户
     */
    User findByIdCard(@Param("idCard") String idCard);

    /**
     * 插入新用户
     */
    int insert(User user);

    /**
     * 更新最后登录时间
     */
    int updateLastLogin(@Param("id") Long id, @Param("lastLogin") LocalDateTime lastLogin);


    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Long id);

    /**
     * 更新用户信息
     */
    int update(User user);
}
