package com.farmwise.rbac.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.common.exception.BizException;
import com.farmwise.rbac.dto.PermissionResponse;
import com.farmwise.rbac.dto.RolePermissionRow;
import com.farmwise.rbac.dto.RoleResponse;
import com.farmwise.rbac.dto.UpdateRolePermissionsRequest;
import com.farmwise.rbac.dto.UpdateUserRolesRequest;
import com.farmwise.rbac.mapper.PermissionMapper;
import com.farmwise.rbac.mapper.RoleMapper;
import com.farmwise.rbac.model.Role;
import com.farmwise.user.dto.UserPermissionRow;
import com.farmwise.user.dto.UserProfile;
import com.farmwise.user.dto.UserRoleRow;
import com.farmwise.user.mapper.UserMapper;
import com.farmwise.user.mapper.UserRoleMapper;
import com.farmwise.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RbacAdminService {
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    private static final String ADMIN = "sys_admin";

    @Transactional(readOnly = true)
    public List<PermissionResponse> listPermissions(String module) {
        module = normalizeOptional(module);

        return permissionMapper.findAll(module)
                .stream()
                .map(PermissionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        List<Role> roles = roleMapper.findAll();
        List<RolePermissionRow> permissions = roleMapper.findAllPermissionRelations();
        Map<String, List<String>> permissionsByRoleCode = permissions.stream()
                .collect(Collectors.groupingBy(RolePermissionRow::roleCode,
                        Collectors.mapping(RolePermissionRow::permissionCode, Collectors.toList())));

        return roles.stream().map(role -> RoleResponse.from(role,
                permissionsByRoleCode.getOrDefault(role.code(), List.of()))).toList();
    }

    @Transactional(readOnly = true)
    public List<UserProfile> listUsers(
            String keyword,
            String status,
            String roleCode) {
        keyword = normalizeOptional(keyword);
        status = normalizeOptional(status);
        roleCode = normalizeOptional(roleCode);

        if (status != null && !"active".equals(status) && !"disabled".equals(status)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的用户状态: " + status);
        }

        List<User> users = userMapper.findByCondition(keyword, status, roleCode);

        if (users.isEmpty()) {
            return List.of();
        }

        List<String> userIds = users.stream().map(user -> user.id()).toList();

        List<UserRoleRow> userRoles = userRoleMapper.findAllByUserIds(userIds);

        List<UserPermissionRow> userPermissions = permissionMapper.findAllByUserIds(userIds);

        Map<String, List<String>> rolesByUserId = userRoles.stream().collect(
                Collectors.groupingBy(UserRoleRow::userId,
                        Collectors.mapping(UserRoleRow::roleCode, Collectors.toList())));

        Map<String, List<String>> permissionsByUserId = userPermissions.stream().collect(
                Collectors.groupingBy(
                        UserPermissionRow::userId,
                        Collectors.mapping(
                                UserPermissionRow::permissionCode,
                                Collectors.toList())));

        return users.stream().map(
                user -> UserProfile.from(user, rolesByUserId.getOrDefault(user.id(), List.of()),
                        permissionsByUserId.getOrDefault(user.id(), List.of())))
                .toList();
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip();
    }

    @Transactional
    public UserProfile updateUserRoles(
            String operatorId,
            String userId,
            UpdateUserRolesRequest request) {
        operatorId = operatorId.strip();
        userId = userId.strip();
        List<String> newRoleCodes = request.roleCodes()
                .stream()
                .map(String::strip)
                .distinct()
                .sorted()
                .toList();

        User user = userMapper.findByIdForUpdate(userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "要修改的用户不存在"));

        List<String> currentRoleCodes = userRoleMapper.findRoleCodesByUserId(userId);

        Set<String> existingRoleSet = new HashSet<>(
                roleMapper.findExistingCodes(newRoleCodes));

        List<String> invalidRoleCodes = newRoleCodes.stream()
                .filter(roleCode -> !existingRoleSet.contains(roleCode))
                .toList();

        if (!invalidRoleCodes.isEmpty()) {
            throw new BizException(
                    HttpStatus.BAD_REQUEST,
                    "不存在的角色编码: "
                            + String.join(", ", invalidRoleCodes));
        }

        if (currentRoleCodes.contains(ADMIN)
                && !newRoleCodes.contains(ADMIN)) {
            if (operatorId.equals(userId)) {
                throw new BizException(HttpStatus.CONFLICT, "管理员用户不能移除自己的管理员身份");
            }

            List<String> adminIds = userRoleMapper.findAdminIdsForUpdate();
            if (adminIds.size() == 1) {
                throw new BizException(HttpStatus.CONFLICT, "系统至少需要保留一个管理员");
            }
        }

        int affectedRows = userRoleMapper.deleteByUserId(userId);

        if (affectedRows != currentRoleCodes.size()) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "更新用户角色失败");
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        affectedRows = userRoleMapper.insertAll(userId, newRoleCodes, now);

        if (affectedRows == 0 || affectedRows != newRoleCodes.size()) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "更新用户角色失败");
        }

        List<String> newRoles = userRoleMapper.findRoleCodesByUserId(userId);
        List<String> permissions = permissionMapper.findAllByUserIds(List.of(userId)).stream()
                .map(UserPermissionRow::permissionCode).toList();

        return UserProfile.from(user, newRoles, permissions);
    }

    @Transactional
    public RoleResponse updateRolePermissions(
            String roleCode,
            UpdateRolePermissionsRequest request) {
        roleCode = roleCode.strip();
        List<String> newPermissionCodes = request.permissionCodes()
                .stream()
                .map(String::strip)
                .distinct()
                .sorted()
                .toList();

        Role role = roleMapper.findByCodeForUpdate(roleCode)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "要修改的角色不存在"));

        if (!newPermissionCodes.isEmpty()) {
            Set<String> existingPermissionSet = new HashSet<>(
                    permissionMapper.findExistingCodes(newPermissionCodes));

            List<String> invalidPermissionCodes = newPermissionCodes.stream()
                    .filter(permissionCode -> !existingPermissionSet.contains(permissionCode))
                    .toList();

            if (!invalidPermissionCodes.isEmpty()) {
                throw new BizException(
                        HttpStatus.BAD_REQUEST,
                        "不存在的权限编码: "
                                + String.join(", ", invalidPermissionCodes));
            }
        }

        List<String> currentPermissionCodes =
                roleMapper.findPermissionCodesByRoleCode(roleCode);

        int affectedRows = roleMapper.deletePermissionsByRoleCode(roleCode);

        if (affectedRows != currentPermissionCodes.size()) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "更新角色权限失败");
        }

        if (!newPermissionCodes.isEmpty()) {
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            affectedRows = roleMapper.insertPermissions(
                    roleCode,
                    newPermissionCodes,
                    now);

            if (affectedRows != newPermissionCodes.size()) {
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "更新角色权限失败");
            }
        }

        List<String> permissions =
                roleMapper.findPermissionCodesByRoleCode(roleCode);

        return RoleResponse.from(role, permissions);
    }
}
