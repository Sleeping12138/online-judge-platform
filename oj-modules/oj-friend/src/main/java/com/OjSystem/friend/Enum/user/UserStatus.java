package com.OjSystem.friend.Enum.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum UserStatus {
    NORMAL(1,"正常"),
    ERROR(0,"拉黑")
    ;
    private Integer code;
    private String status;
}
