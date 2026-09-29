package com.example.rabc.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenData {
    private Long userId;
    private  String username;
    private List<String> permList;
    //扩展
}
