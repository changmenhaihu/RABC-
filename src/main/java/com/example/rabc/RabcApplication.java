package com.example.rabc;

import org.mindrot.jbcrypt.BCrypt;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.rabc.mapper")
public class RabcApplication {

    public static void main(String[] args) {
        String hashPw = BCrypt.hashpw("123456",BCrypt.gensalt(10));
        System.out.println("加密后的密码："+hashPw);
        SpringApplication.run(RabcApplication.class, args);
    }
}
