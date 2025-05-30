package com.OjSystem.system.domain.sysuser;

import com.OjSystem.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_sys_user")
public class SysUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;  //主键  不再使用auto_increment

    private String userAccount;

    private String password;

    private String nickName;
}