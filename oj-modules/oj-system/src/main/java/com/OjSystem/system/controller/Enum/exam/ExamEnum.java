package com.OjSystem.system.controller.Enum.exam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ExamEnum {

    PUBLISHED(1, "已发布"),
    UNPUBLISHED(0, "未发布"),
    ;

    private Integer code;
    private String message;
}
