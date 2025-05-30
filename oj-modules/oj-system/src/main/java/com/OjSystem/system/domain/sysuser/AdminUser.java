package com.OjSystem.system.domain.sysuser;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUser {
    @NotBlank(message = "账号不能为空")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    private String password;
}
