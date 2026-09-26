package com.example.rabc.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthFilter implements Filter{
  public static  final Map<String,Long> TOKEN_MAP =  new ConcurrentHashMap<>();

    //@Override
    public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException{
      HttpServletRequest req = (HttpServletRequest) request;
      HttpServletResponse resq = (HttpServletResponse) response;

      //放行登录接口不校验token
      String uri = req.getRequestURI();
      if("/login".equals(uri)){
          chain.doFilter(request,response);
          return;
      }
      //从请求头获取token，格式为Authorization:Bearer xx-token
        String token = req.getHeader("Authorization");
      if(!StringUtils.hasText(token)){
          req.getContentType();
          resq.getWriter().write("未登录，请先获取token");
          return;}
        //如果存在Bearer 前缀去掉
        if(token.startsWith("Bearer")){
            token = token.substring(7);
        }
        //判断token是否存在
        if(!TOKEN_MAP.containsKey(token)){
            resq.setContentType("text/plain;charset = utf8");
            resq.getWriter()
                    .write("token无效。请重新登录");
        return;}
        chain.doFilter(request,response);
  }
}
