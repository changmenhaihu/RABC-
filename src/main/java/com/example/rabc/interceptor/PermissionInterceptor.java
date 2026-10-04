package com.example.rabc.interceptor;

import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.filter.AuthFilter;
import com.example.rabc.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception{
        //1.如果不是映射到方法上 直接放行（访问静态资源
        if(!(handler instanceof HandlerMethod handlerMethod)){
            return  true;
        }
        //2.获取方法上的@RequirePerm 注解
        RequirePerm requirePerm = handlerMethod.getMethodAnnotation(RequirePerm.class);
        //如果方法上没有注解 说明不需要特定鉴权 直接放行
        if (requirePerm == null){
            return true;
        }
        //从request 域去出 filter 提前放入的权限列表 不再查询Redis
        List<String> permList = (List<String>) request.getAttribute(AuthFilter.REQUEST_ATTR_PERM_LIST);
        if (permList == null || permList.contains(requirePerm.value())){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":403,\"msg\":\"权限不足，禁止访问\",\"data\":null}");
            return false;
        }
        return true;//有权限  放行
    }
}
