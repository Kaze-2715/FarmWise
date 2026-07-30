package com.farmwise.user.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.farmwise.user.dto.UserRoleRow;

@Mapper
public interface UserRoleMapper {

    @Insert("""
            INSERT INTO user_roles (
                user_id,
                role_code,
                created_at
            ) VALUES (
                #{userId},
                #{roleCode},
                #{createdAt}
            )
            """)
    int insert(
            @Param("userId") String userId,
            @Param("roleCode") String roleCode,
            @Param("createdAt") LocalDateTime createdAt);

    @Select("""
            SELECT role_code
            FROM user_roles
            WHERE user_id = #{userId}
            ORDER BY role_code
            """)
    List<String> findRoleCodesByUserId(@Param("userId") String userId);

    @Select("""
            SELECT DISTINCT rp.permission_code
            FROM user_roles ur
            JOIN role_permissions rp ON rp.role_code = ur.role_code
            WHERE ur.user_id = #{userId}
            ORDER BY rp.permission_code
            """)
    List<String> findPermissionCodesByUserId(@Param("userId") String userId);

    @Select("""
            <script>
            SELECT
                user_id,
                role_code
            FROM user_roles
            WHERE user_id IN
            <foreach collection="userIds" item="userId" open="(" separator="," close=")">
                #{userId}
            </foreach>
            ORDER BY user_id ASC, role_code ASC
            </script>
            """)
    List<UserRoleRow> findAllByUserIds(
            @Param("userIds") List<String> userIds);

    @Select("""
            SELECT user_id
            FROM user_roles
            WHERE role_code = 'sys_admin'
            ORDER BY user_id
            FOR UPDATE
            """)
    List<String> findAdminIdsForUpdate();

    @Delete("""
            DELETE
            FROM user_roles
            WHERE user_id = #{userId}
            """)
    int deleteByUserId(@Param("userId") String userId);

    @Insert("""
            <script>
            INSERT INTO user_roles (
                user_id,
                role_code,
                created_at
            ) VALUES
            <foreach collection="roleCodes" item="roleCode" separator=",">
                (#{userId}, #{roleCode}, #{createdAt})
            </foreach>
            </script>
            """)
    int insertAll(
            @Param("userId") String userId,
            @Param("roleCodes") List<String> roleCodes,
            @Param("createdAt") LocalDateTime createdAt);
}
