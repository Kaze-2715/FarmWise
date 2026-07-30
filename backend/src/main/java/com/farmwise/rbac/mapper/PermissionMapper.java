package com.farmwise.rbac.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.farmwise.rbac.model.Permission;
import com.farmwise.user.dto.UserPermissionRow;

@Mapper
public interface PermissionMapper {
    @Select("""
            <script>
            SELECT
                code,
                name,
                module,
                description
            FROM permissions
            <where>
                <if test="module != null">
                    AND module = #{module}
                </if>
            </where>
            ORDER BY module ASC, code ASC
            </script>
            """)
    List<Permission> findAll(@Param("module") String module);

    @Select("""
            <script>
            SELECT DISTINCT
                ur.user_id,
                rp.permission_code
            FROM user_roles ur
            JOIN role_permissions rp
            ON ur.role_code = rp.role_code
            WHERE ur.user_id IN
            <foreach collection="userIds" item="userId" open="(" separator="," close=")">
                #{userId}
            </foreach>
            ORDER BY ur.user_id, rp.permission_code
            </script>
            """)
    List<UserPermissionRow> findAllByUserIds(
            @Param("userIds") List<String> userIds);

    @Select("""
            <script>
            SELECT code
            FROM permissions
            WHERE code IN
            <foreach collection="permissionCodes" item="permissionCode" open="(" separator="," close=")">
                #{permissionCode}
            </foreach>
            </script>
            """)
    List<String> findExistingCodes(
            @Param("permissionCodes") List<String> permissionCodes);
}
