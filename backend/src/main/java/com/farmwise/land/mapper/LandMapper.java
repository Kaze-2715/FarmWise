package com.farmwise.land.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.land.model.Land;

@Mapper
public interface LandMapper {
    @Select("""
            SELECT *
            FROM lands
            WHERE owner_id = #{ownerId}
            ORDER BY created_at DESC, id DESC
            """)
    List<Land> findAllByOwnerId(
            @Param("ownerId") String ownerId);

    @Insert("""
            INSERT INTO lands (
                id, owner_id, name, land_type, area, crop,
                status, location, longitude, latitude,
                created_at, updated_at
            ) VALUES (
                #{id},
                #{ownerId},
                #{name},
                #{landType},
                #{area},
                #{crop},
                #{status},
                #{location},
                #{longitude},
                #{latitude},
                #{createdAt},
                #{updatedAt}
            )
            """)
    int addLand(Land land);

    @Select("""
            SELECT *
            FROM lands
            WHERE id = #{landId}
              AND owner_id = #{ownerId}
            """)
    Optional<Land> findByIdAndOwnerId(
            @Param("landId") String landId,
            @Param("ownerId") String ownerId);

    @Update("""
            UPDATE lands
            SET name = #{name},
                land_type = #{landType},
                area = #{area},
                crop = #{crop},
                status = #{status},
                location = #{location},
                longitude = #{longitude},
                latitude = #{latitude},
                updated_at = #{updatedAt}
            WHERE id = #{id}
              AND owner_id = #{ownerId}
            """)
    int updateLand(Land land);

    @Delete("""
            DELETE FROM lands
            WHERE id = #{landId}
              AND owner_id = #{ownerId}
            """)
    int deleteByIdAndOwnerId(
            @Param("landId") String landId,
            @Param("ownerId") String ownerId);
}
