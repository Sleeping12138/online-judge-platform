package com.OjSystem.friend.Enum.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ProgramType {
    JAVA(0, "java语言"),

    CPP(1, "C++语言"),

    GOLANG(2, "go语言");

    private Integer value;

    private String desc;
}
