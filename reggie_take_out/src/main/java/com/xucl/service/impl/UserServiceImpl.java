package com.xucl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xucl.entity.User;
import com.xucl.mapper.UserMapper;
import com.xucl.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/28 11:06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
