package com.itheima.controller;

import com.itheima.domain.db.User;
import com.itheima.domain.db.UserInfo;
import com.itheima.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserManager userManager;

    // 保存用户
    @PostMapping("/user/save")
    public ResponseEntity save(@RequestBody User user) {
        return userManager.save(user);
    }

    // 查询用户
    @GetMapping("/user/findByPhone")
    public ResponseEntity findByPhone(String phone) {
        return userManager.findByPhone(phone);
    }

    //登陆
    @PostMapping("/user/login")
    public void sendSms(@RequestBody Map<String,String> parm) {
        //获取手机号
        String phone = parm.get("phone");
        //调用manger发送短信
        userManager.sendSms(phone);
    }
    //注册,登陆功能
    @PostMapping("/user/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String,String> parm){
        //获取手机号和验证码
        String phone = parm.get("phone");
        String verificationCode = parm.get("verificationCode");
        //调用manger
       return userManager.loginVerification(phone,verificationCode);
    }
    //完善用户信息
    @PostMapping("/user/loginReginfo")
    public void saveUserInfoBase(@RequestBody UserInfo userInfo ,@RequestHeader("Authorization") String token){
        //调用manger
        userManager.saveUserInfoBase(userInfo,token);
    }
    //完善用户头像信息
    @PostMapping("/user/loginReginfo/head")
    public ResponseEntity saveUserInfoHead(MultipartFile headPhoto, @RequestHeader("Authorization") String token) throws Exception{
        //调用manger
      return userManager.saveUserInfoHead(headPhoto,token);
    }
    //查询用户信息
    @GetMapping("/users")
    public ResponseEntity findUserInfoVo(Long userID,Long huanxinID, @RequestHeader("Authorization") String token){
        //调用manger
        if (userID!=null) {
            return userManager.findUserInfoVo(userID);
        }else if(huanxinID!=null){
            return userManager.findUserInfoVo(huanxinID);
        }else{
            User user = userManager.findUserByToken(token);
            return userManager.findUserInfoVo(user.getId());
        }
    }

}
