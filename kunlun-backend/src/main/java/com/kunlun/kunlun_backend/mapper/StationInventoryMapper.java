/**
 * 站点库存表数据访问接口
 */

package com.kunlun.kunlun_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.kunlun_backend.entity.StationInventory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StationInventoryMapper extends BaseMapper<StationInventory> {
}
