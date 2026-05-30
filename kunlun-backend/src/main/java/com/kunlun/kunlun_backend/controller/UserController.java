/**
 * 用户管理控制器 —— CRUD + 分页查询
 * 更新时密码为空则不修改密码字段
 */

package com.kunlun.kunlun_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.kunlun_backend.common.Result;
import com.kunlun.kunlun_backend.entity.User;
import com.kunlun.kunlun_backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @GetMapping
    public Result<IPage<User>> page(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "20") int size,
                                     @RequestParam(required = false) String username,
                                     @RequestParam(required = false) String role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(StringUtils.hasText(username), User::getUsername, username)
                .eq(StringUtils.hasText(role), User::getRole, role)
                .orderByAsc(User::getRole, User::getUsername);
        return Result.ok(userMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id) {
        return Result.ok(userMapper.selectById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody User user) {
        userMapper.insert(user);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody User user) {
        // 密码为空时不修改
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(null);
        }
        user.setId(id);
        userMapper.updateById(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        userMapper.deleteById(id);
        return Result.ok();
    }
}
