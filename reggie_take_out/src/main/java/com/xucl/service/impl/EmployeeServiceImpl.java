package com.xucl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xucl.entity.Employee;
import com.xucl.mapper.EmployeeMapper;
import com.xucl.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/22 15:07
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
