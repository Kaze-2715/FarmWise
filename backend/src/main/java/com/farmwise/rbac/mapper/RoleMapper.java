package com.farmwise.rbac.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.farmwise.rbac.dto.RolePermissionRow;
import com.farmwise.rbac.model.Role;

@Mapper
public interface RoleMapper {
    @Select("""
            SELECT
                code,
                name,
                description,
                created_at
            FROM roles
            ORDER BY code ASC
            """)
    List<Role> findAll();

    @Select("""
            SELECT
                role_code,
                permission_code
            FROM role_permissions
            ORDER BY role_code ASC, permission_code ASC
            """)
    List<RolePermissionRow> findAllPermissionRelations();

    @Select("""
            <script>
            SELECT code
            FROM roles
            WHERE code IN
            <foreach collection="roleCodes" item="roleCode" open="(" separator="," close=")">
                #{roleCode}
            </foreach>
            </script>
            """)
    List<String> findExistingCodes(
            @Param("roleCodes") List<String> roleCodes);

    @Select("""
            SELECT
                code,
                name,
                description,
                created_at
            FROM roles
            WHERE code = #{roleCode}
            FOR UPDATE
            """)
    Optional<Role> findByCodeForUpdate(
            @Param("roleCode") String roleCode);

    @Select("""
            SELECT permission_code
            FROM role_permissions
            WHERE role_code = #{roleCode}
            ORDER BY permission_code
            """)
    List<String> findPermissionCodesByRoleCode(
            @Param("roleCode") String roleCode);

    @Delete("""
            DELETE FROM role_permissions
            WHERE role_code = #{roleCode}
            """)
    int deletePermissionsByRoleCode(
            @Param("roleCode") String roleCode);

    @Insert("""
            <script>
            INSERT INTO role_permissions (
                role_code,
                permission_code,
                created_at
            ) VALUES
            <foreach collection="permissionCodes" item="permissionCode" separator=",">
                (
                    #{roleCode},
                    #{permissionCode},
                    #{createdAt}
                )
            </foreach>
            </script>
            """)
    int insertPermissions(
            @Param("roleCode") String roleCode,
            @Param("permissionCodes") List<String> permissionCodes,
            @Param("createdAt") LocalDateTime createdAt);
}
