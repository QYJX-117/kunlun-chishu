/**
 * PredictionLog表数据访问接口
 */

package com.kunlun.kunlun_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.kunlun_backend.entity.PredictionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface PredictionLogMapper extends BaseMapper<PredictionLog> {

    @Select("SELECT model_name, COUNT(*) AS count, AVG(mape) AS avg_mape " +
            "FROM prediction_log WHERE actual_value IS NOT NULL " +
            "GROUP BY model_name ORDER BY avg_mape ASC")
    List<Map<String, Object>> modelAccuracyStats();

    @Select("SELECT * FROM prediction_log WHERE station_id = #{stationId} " +
            "AND material_id = #{materialId} ORDER BY predict_date DESC LIMIT 1")
    PredictionLog findLatest(@Param("stationId") Integer stationId,
                             @Param("materialId") Integer materialId);
}
