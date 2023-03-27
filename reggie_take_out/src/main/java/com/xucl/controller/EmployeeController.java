package com.xucl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xucl.common.R;
import com.xucl.entity.Employee;
import com.xucl.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.Name;
import java.time.LocalDateTime;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/22 15:09
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /** 
     * TODO
     * 员工登录
     * @param request
     * @param employee 
     * @return com.xucl.common.R<com.xucl.entity.Employee>
     * @date   2023年03月22日15:22:30
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        // 1. 将页面提交的密码password进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的用户名username查询数据库数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);

        // 3. 如果没有查询到返回登录失败结果
        if (one == null){
            return R.error("登录失败");
        }

        // 4. 密码对比，如果不一致登录失败
        if (!one.getPassword().equals(password)){
            return R.error("登录失败");
        }

        // 5. 查看员工状态，如果为已禁用状态，返回员工已禁用结果
        if (one.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        // 6. 登录成功，将员工id存入sesson并返回成功结果
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }

    /**
     * TODO
     * 员工退出
     * @param request
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理sesson 中保存的当前登录员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * TODO
     * 新增员工
     * @param employe
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employe){
        log.info("新增员工信息，{}",employe.toString());

        //设置初始密码123456，需要进行md5 加密
        employe.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // employe.setCreateTime(LocalDateTime.now());
        // employe.setUpdateTime(LocalDateTime.now());
        // long id = (long) request.getSession().getAttribute("employee");
        // employe.setCreateUser(id);
        // employe.setUpdateUser(id);
        employeeService.save(employe);

        return R.success("新增员工成功");
    }

    /**
     * TODO
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return com.xucl.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @date
     */
    @GetMapping("/page")
    public R<Page>  page(int page,int pageSize,String name){
        log.info("page = {}, pageSize = {} ,name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.hasLength(name),Employee::getName, name);
        //添加排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * TODO
     *  更新员工权限
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("员工权限更新:id = {}",employee);
        long id2 = Thread.currentThread().getId();
        log.info(id2 + "当前线程");
        employee.setUpdateTime(LocalDateTime.now());
        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(id);
        boolean update = employeeService.updateById(employee);
        if (update) {
            return R.success("员工信息修改成功");
        }
        return R.error("修改失败");
    }

    /**
     * TODO
     * 根据ID 查询员工信息
     * @param id
     * @return com.xucl.common.R<com.xucl.entity.Employee>
     * @date
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable String id) {
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return R.error("查询失败");
        }
        return R.success(employee);
    }
}
