package com.itheima.service.db.impl;

import com.itheima.domain.db.User;
import com.itheima.domain.db.UserInfo;
import com.itheima.mapper.UserInfoMapper;
import com.itheima.mapper.UserMapper;
import com.itheima.service.UserInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    //用户信息保存
    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }
    //用户信息更新
    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }
    //查询用户信息
    @Override
    public UserInfo findUserById(Long id) {
        return userInfoMapper.selectById(id);
    }
}
