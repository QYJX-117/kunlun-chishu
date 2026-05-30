/**
 * SalesHistory表数据访问接口
 */

package com.kunlun.kunlun_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.kunlun_backend.entity.SalesHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SalesHistoryMapper extends BaseMapper<SalesHistory> {

    @Select("SELECT DISTINCT sale_date, sales_volume, is_anomaly FROM sales_history " +
            "WHERE station_id = #{stationId} AND material_id = #{materialId} " +
            "AND sale_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "AND is_anomaly = 0 ORDER BY sale_date ASC")
    List<SalesHistory> findRecentNormal(@Param("stationId") Integer stationId,
                                         @Param("materialId") Integer materialId,
                                         @Param("days") Integer days);

    @Select("SELECT AVG(sales_volume) FROM sales_history " +
            "WHERE station_id = #{stationId} AND material_id = #{materialId} " +
            "AND is_anomaly = 0 AND sale_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY)")
    Double avgVolume(@Param("stationId") Integer stationId,
                     @Param("materialId") Integer materialId,
                     @Param("days") Integer days);

    @Select("SELECT STDDEV(sales_volume) FROM sales_history " +
            "WHERE station_id = #{stationId} AND material_id = #{materialId} " +
            "AND is_anomaly = 0 AND sale_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY)")
    Double stdVolume(@Param("stationId") Integer stationId,
                     @Param("materialId") Integer materialId,
                     @Param("days") Integer days);
}
