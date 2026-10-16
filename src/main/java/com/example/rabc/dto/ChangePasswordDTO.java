package com.example.rabc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotBlank(message = "原密码")
    @Size(min = 6, max = 32, message = "密码长度 6-32")
    private String oldPassword;

    @NotBlank(message = "新密码")
    private String newPassword;

    @NotBlank(message = "确认新密码")
    private String confirmPassword;

}
