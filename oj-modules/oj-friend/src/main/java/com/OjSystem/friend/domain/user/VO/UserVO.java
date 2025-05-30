package com.OjSystem.friend.domain.user.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String nickName;

    private String headImage;

    private Integer sex;

    private String phone;

    private String email;

    private String wechat;

    private String schoolName;

    private String majorName;

    private String introduce;

    private Integer status;
}
