package com.example.rabc.filter;

import com.example.rabc.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFilter implements Filter{
   //过滤器初始化
   public final UserService userService;
   public AuthFilter(UserService userService){
       this.userService = userService;
   }
   @Override
    public void doFilter(ServletRequest request,ServletResponse response,
                         FilterChain chain) throws ServletException, IOException {
       //转成http请求，响应对象
      HttpServletRequest req;
      HttpServletResponse resp;
      try {
          req = (HttpServletRequest) request;
          resp = (HttpServletResponse)  response;
      }catch (ClassCastException e){
          // 不是http请求 放行或者中断
          chain.doFilter(request,response);
          return;
      }
       //设置返回内容的编码 避免中文乱码
       resp.setContentType("test/plain;charse=utf-8");
       //拿到请求地址 如果是login 那么直接放行
       String uri = req.getRequestURI();
       if("/login".equals(uri)){
           chain.doFilter(request,response);
           return;
       }
       //读取请求头里的Authorization
       String authorization = req.getHeader("Authorization");
       if(authorization == null || authorization.isBlank()){
           resp.getWriter().write("未登录，需先获取token");
           return;
       }
       //处理Bearer前缀
       String token;
       if(authorization.startsWith("Bearer")){
           token = authorization.substring(7);
       }else {
           token = authorization;
       }
       boolean vaild = UserService.checkToken(token);
       if(!vaild){
           resp.getWriter().write("token无效，请重新登录");
           return;
       }
       //上面的校验通过请求继续往后走
       chain.doFilter(request,response);
   }
}
