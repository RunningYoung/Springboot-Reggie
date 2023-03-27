package com.xucl.filter;

import com.alibaba.fastjson.JSON;
import com.xucl.common.BaseContext;
import com.xucl.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xucl
 * @apiNote 过滤器
 * @date 2023/3/22 17:44
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = {"/*"})
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        long id = Thread.currentThread().getId();
        log.info(id + "当前线程");
        //1. 获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);
        //定义不需要处理的请求路径
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
        };
        //2. 判断本次请求是否需要处理
        boolean check = checkURI(urls, requestURI);

        //3. 如果不需要处理，则直接放行
        if (check) {
            log.info("本地请求不需要处理：{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4. 判断登录状态，如果已经登录，则直接放行
        boolean employee = request.getSession().getAttribute("employee") != null;
        if (employee) {
            log.info("用户已登录：{}",request.getSession().getAttribute("employee"));
            Long id2 = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id2);
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5. 如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    //判断本地请求是否放行
    public boolean checkURI(String[] urls,String uri) {
        for (String url: urls ){
            boolean b = PATH_MATCHER.match(url, uri);
            if (b) {
                return b;
            }
        }
        return false;
    }
}
