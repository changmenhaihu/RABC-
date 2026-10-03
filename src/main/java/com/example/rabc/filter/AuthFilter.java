package com.example.rabc.filter;

import com.example.rabc.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
//import java.util.concurrent.TimeUnit;

@Component
public class AuthFilter implements Filter{
   //过滤器初始化
   public final UserService userService;

    // private static  final  String TOKEN_PREFIX= "token:";
  // private static final long TOKEN_EXPIRE_SECONDS = 2*60*60;
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
       resp.setContentType("application/json;charse=utf-8");
       //拿到请求地址 如果是login 那么直接放行
       String uri = req.getRequestURI();
       if("/login".equals(uri)){
           chain.doFilter(request,response);
           return;
       }
       //读取请求头里的Authorization
       String authorization = req.getHeader("Authorization");
       if(authorization == null || authorization.isBlank()){
           ((HttpServletResponse) response).setStatus(401);
           response.setContentType("application/json;charset = utf-8");
           resp.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先获取token\",\"data\":null}");
           return;
       }
       //处理Bearer前缀
       String token;
       if(authorization.startsWith("Bearer")){
           token = authorization.substring(7);
       }else {
           token = authorization;
       }
       boolean valid = userService.checkToken(token);
       if(!valid){
           resp.getWriter().write("{\"code\":401,\"msg\":\"token无效，请重新登录\",\"data\":null}");
           return;
       }
       //token 续期 每次合法访问就刷新过期时间
     //  String redisKey = TOKEN_PREFIX + token;
      // redisTemplate.expire(redisKey,TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
       //上面的校验通过请求继续往后走
       chain.doFilter(request,response);

   }

}
