package com.farmwise.user.mapper;

import java.time.LocalDateTime;
import java.util.Optional;

import com.farmwise.user.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("""
            SELECT EXISTS (
                SELECT *
                FROM users
                WHERE email = #{email}
            )
            """)
    boolean existsByEmail(@Param("email") String email);

    @Select("""
            SELECT EXISTS (
                SELECT *
                FROM users
                WHERE username = #{username}
            )
            """)
    boolean existsByUsername(@Param("username") String username);

    @Insert("""
            INSERT INTO users (
                id,
                username,
                password_hash,
                real_name,
                email,
                email_verified,
                phone,
                avatar_file_id,
                organization,
                province,
                city,
                position,
                status,
                created_at,
                updated_at,
                last_login_at
            ) VALUES (
                #{id},
                #{username},
                #{passwordHash},
                #{realName},
                #{email},
                #{emailVerified},
                #{phone},
                #{avatarFileId},
                #{organization},
                #{province},
                #{city},
                #{position},
                #{status},
                #{createdAt},
                #{updatedAt},
                #{lastLoginAt}
            )
            """)
    int insert(User user);

    @Select("""
            SELECT *
            FROM users
            WHERE id = #{id}
            """)
    Optional<User> findById(@Param("id") String id);

    @Select("""
            SELECT *
            FROM users
            WHERE email = #{email}
            """)
    Optional<User> findByEmail(@Param("email") String email);

    @Select("""
            SELECT *
            FROM users
            WHERE username = #{username}
            """)
    Optional<User> findByUsername(@Param("username") String username);

    @Update("""
            UPDATE users
            SET last_login_at = #{lastLoginAt}
            WHERE id = #{userId}
            """)
    int updateLastLoginAt(
            @Param("userId") String userId,
            @Param("lastLoginAt") LocalDateTime lastLoginAt);

    @Select("""
            SELECT EXISTS(
                SELECT * FROM users WHERE username = #{username} AND id <> #{userId}
            )
            """)
    boolean existsByUsernameAndIdNot(
            @Param("username") String username,
            @Param("userId") String userId);

    @Select("""
            SELECT EXISTS(
                SELECT * FROM users WHERE email = #{email} AND id <> #{userId}
            )
            """)
    boolean existsByEmailAndIdNot(
            @Param("email") String email,
            @Param("userId") String userId);

    @Update("""
            UPDATE users
            SET username = #{username},
                real_name = #{realName},
                email = #{email},
                email_verified = #{emailVerified},
                phone = #{phone},
                avatar_file_id = #{avatarFileId},
                organization = #{organization},
                province = #{province},
                city = #{city},
                position = #{position},
                updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    int updateProfile(User user);
}
