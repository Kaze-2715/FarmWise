package com.farmwise.file.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

import com.farmwise.file.model.FileMetadata;

@Mapper
public interface FileMapper {
    @Select("""
            SELECT EXISTS (
                SELECT * 
                FROM files
                WHERE id = #{fileId} AND
                    owner_id = #{ownerId} AND
                    purpose = 'avatar')
            """)
    boolean existsAvatarByIdAndOwnerId(
        @Param("fileId") String fileId,
        @Param("ownerId") String ownerId
    );

    @Insert("""
            INSERT INTO files (
                id,
                owner_id,
                original_name,
                content_type,
                size,
                purpose,
                storage_provider,
                storage_key,
                created_at
            ) VALUES (
                #{id},
                #{ownerId},
                #{originalName},
                #{contentType},
                #{size},
                #{purpose},
                #{storageProvider},
                #{storageKey},
                #{createdAt}
            )
            """)
    int insert(FileMetadata file);

    @Select("""
            SELECT *
            FROM files
            WHERE id = #{id}
            """)
    Optional<FileMetadata> findById(
            @Param("id") String id);
}
