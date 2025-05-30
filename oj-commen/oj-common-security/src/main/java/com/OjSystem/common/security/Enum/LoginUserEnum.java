package com.OjSystem.common.security.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum LoginUserEnum {
    NORMAL(1,"普通用户"),
    ADMIN(2,"管理员");

    private Integer code;
    private String description;
}