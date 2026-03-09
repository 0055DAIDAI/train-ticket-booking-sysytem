// src/main/java/org/example/mapper/AdminMapper.java
package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.Admin;

import java.time.LocalDateTime;

@Mapper
public interface AdminMapper {
    /**
     * 根据用户名查询管理员
     */
    Admin findByAdminName(@Param("adminName") String adminName);

    /**
     * 根据手机号查询管理员
     */
    Admin findByPhone(@Param("phone") String phone);

    /**
     * 插入新管理员
     */
    int insert(Admin admin);

    /**
     * 更新最后登录时间
     */
    int updateLastLogin(@Param("id") Long id, @Param("lastLogin") LocalDateTime lastLogin);

    /**
     * 根据ID查询管理员
     */
    Admin findById(@Param("id") Long id);

    /**
     * 更新管理员信息
     */
    int update(Admin admin);
}
