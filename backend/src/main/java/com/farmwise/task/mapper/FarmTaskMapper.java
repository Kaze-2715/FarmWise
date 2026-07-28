package com.farmwise.task.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FarmTaskMapper {
    @Select("""
            SELECT EXISTS (
                SELECT *
                FROM farm_tasks
                WHERE source_type = #{sourceType}
                AND source_id = #{sourceId}
            )""")
    boolean existsBySourceTypeAndSourceId(
            @Param("sourceType") String sourceType,
            @Param("sourceId") String sourceId);
}
