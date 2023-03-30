package com.xucl.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xucl.common.BaseContext;
import com.xucl.common.R;
import com.xucl.entity.User;
import com.xucl.service.UserService;
import com.xucl.utils.SMSUtils;
import com.xucl.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/28 11:08
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (!StringUtils.isEmpty(phone)){
            //生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code==={}",code);
            //调用阿里云提供的短信服务API完成发送短信
            // SMSUtils.sendMessage(phone, code);
            //需要将生成的验证码保存起来
            session.setAttribute(phone,code);
            ValueOperations valueOperations = redisTemplate.opsForValue();
            //将生成的验证码缓存到redis中，并设置有效期为5分钟
            valueOperations.set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取手机号
        log.info("login==={}",map);
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取验证码
        // Object codeSession = session.getAttribute(phone);
        //从redis中获取验证码
        Object codeSession = redisTemplate.opsForValue().get(phone);
        //进行验证码比对 页面提交和session对比
        if (codeSession != null && codeSession.equals(code)) {
            //如果对比成功，登录成功
            //手机号是否在用户表中 如果不在表中 保存手机号 自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            BaseContext.setCurrentId(user.getId());
            //如果用户登录成功删除redis缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
