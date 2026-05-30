/**
 * 用户表数据访问接口
 */

package com.kunlun.kunlun_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.kunlun_backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
