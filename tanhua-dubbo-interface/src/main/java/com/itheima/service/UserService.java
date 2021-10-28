package com.itheima.service;

import com.itheima.domain.db.User;

public interface UserService {
    //保存
    Long save(User user);

    //根据手机查找
    User findByPhone(String phone);
}
