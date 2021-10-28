package com.itheima.service.db.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.domain.db.User;
import com.itheima.mapper.UserMapper;
import com.itheima.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;


@DubboService
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    //新增用户
    public Long save(User user) {
        //对用户密码进行加密
        String md5Pwd = SecureUtil.md5(user.getPassword());
        user.setPassword(md5Pwd);
        //指定时间
        user.setCreated(new Date()); //用户创建时间
        user.setUpdated(new Date()); //用户更新时间
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    //查询用户
    public User findByPhone(String phone) {
        //创建查询对象
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //指定查询条件
        queryWrapper.eq("phone",phone);
        //反回查询结果
        return userMapper.selectOne(queryWrapper);
    }
}
