package com.itheima.service;


import com.itheima.domain.db.User;
import com.itheima.domain.db.UserInfo;

public interface UserInfoService {

    // 保存用户信息
    void save(UserInfo userInfo);

    //更新用户信息
    void update(UserInfo userInfo);

    //查询用户信息
    UserInfo findUserById(Long id);
}

