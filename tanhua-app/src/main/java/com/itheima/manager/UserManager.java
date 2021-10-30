package com.itheima.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itheima.autoconfig.face.AipFaceTemplate;
import com.itheima.autoconfig.oss.OssTemplate;
import com.itheima.autoconfig.sms.SmsProperties;
import com.itheima.autoconfig.sms.SmsTemplate;
import com.itheima.domain.db.User;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.vo.ErrorResult;
import com.itheima.domain.vo.UserInfoVo;
import com.itheima.service.UserInfoService;
import com.itheima.service.UserService;
import com.itheima.uitl.ConstantUtil;
import com.itheima.uitl.JwtUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserManager {

    @DubboReference
    private UserService userService;

    @DubboReference
    private UserInfoService userInfoService;

    //查询
    public ResponseEntity findByPhone(String phone) {
        try {
            // int i = 1/0;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ErrorResult.error());
        }
        User user = userService.findByPhone(phone);
        return ResponseEntity.ok(user);
    }


    //新增
    public ResponseEntity save(User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 发送短信验证码
    public void sendSms(String phone) {
        //生成验证码
        String smsCode = RandomUtil.randomNumbers(6);
        //写死验证码,方便测试
        smsCode = "123456";

      /*  //调用阿里云发送
        smsTemplate.sendSms(phone,smsCode);*/

        //将验证码存入Redis中,并设置存活时间为5分钟
        stringRedisTemplate.opsForValue().set(ConstantUtil.SMS_CODE + phone, smsCode, Duration.ofMinutes(5));

    }

    public ResponseEntity loginVerification(String phone, String verificationCode) {
        //声明反回结果
        Map<String, Object> resultMap = new HashMap<>();
        //从Redis获取验证码
        String codeFromRedis = stringRedisTemplate.opsForValue().get(ConstantUtil.SMS_CODE + phone);

        //对比验证码是否一致
        if (!StrUtil.equals(verificationCode, codeFromRedis)) {
            ResponseEntity.status(500).body(ErrorResult.loginError());
        }
        //根据手机号查询用户
        User user = userService.findByPhone(phone);
        if (user != null) { //如果查到了证明有数据,有数据为老用户
            resultMap.put("isNew", false);
        } else {  //如果查不到就是新用户
            resultMap.put("isNew",true);
            //新用户保存数据库
            user = new User();
            user.setPhone(phone);
            user.setPassword(ConstantUtil.INIT_PASSWORD);
            //调用save方法保存用户
            Long newUserId = userService.save(user);
            user.setId(newUserId);
        }
        //制作JWT令牌
        user.setPassword(null);
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(user);
        String token = JwtUtil.createToken(stringObjectMap);
        // 5.redis存储用户信息
        String json = JSON.toJSONString(user);
        stringRedisTemplate.opsForValue().set(ConstantUtil.USER_TOKEN + token, json, Duration.ofDays(7));
        // 清除短信验证码
        stringRedisTemplate.delete(ConstantUtil.SMS_CODE + phone);

        // 6.返回结果
        resultMap.put("token", token);
        return ResponseEntity.ok(resultMap);
    }
    //完善用户信息
    public void saveUserInfoBase(UserInfo userInfo, String token) {
        //解析token获得user对象
        User userByToken = findUserByToken(token);
        //设置用户信息的ID
        userInfo.setId(userByToken.getId());
        //调用service保存
        userInfoService.save(userInfo);
    }

    //根据token查找对象
    public User findUserByToken(String token){
        //token非空判断
        if (StrUtil.isEmpty(token)){return null;}
        //获取Redis中的user对象
        String json = stringRedisTemplate.opsForValue().get(ConstantUtil.USER_TOKEN + token);
        //对JSON进行非空判断
        if (StrUtil.isEmpty(json)){return null;}
        //吧JSON转换为user对象
        User user = JSONObject.parseObject(json, User.class);
        //对Redis的user对象进行续期
        stringRedisTemplate.opsForValue().set(ConstantUtil.USER_TOKEN+token,json,Duration.ofDays(7));
        return user;
    }

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @Autowired
    private OssTemplate ossTemplate;

    //完善用户头像
    public ResponseEntity saveUserInfoHead(MultipartFile headPhoto, String token) throws Exception {
        //解析token获取user对象
        User userByToken = findUserByToken(token);
        //人脸识别
        boolean detect = aipFaceTemplate.detect(headPhoto.getBytes());
        if (!detect){
            ResponseEntity.status(500).body(ErrorResult.faceError()); //反回非人脸错误信息
        }
        //上传图片到oss,并反回图片地址
        String headPhotoUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
        //封装信息到userinfo对象
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userByToken.getId());
        userInfo.setAvatar(headPhotoUrl);
        userInfo.setCoverPic(headPhotoUrl);
        //调用service更新
        userInfoService.save(userInfo);
        //无信息直接反回成功状态码
        return ResponseEntity.ok(null);
    }
    //查询用户信息
    public ResponseEntity findUserInfoVo(Long id) {
        //根据userID调用service查询
        UserInfo userInfo = userInfoService.findUserById(id);
        //封装到userInfoVo
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtil.copyProperties(userInfo,userInfoVo);

        return ResponseEntity.ok(userInfoVo);
    }
}

