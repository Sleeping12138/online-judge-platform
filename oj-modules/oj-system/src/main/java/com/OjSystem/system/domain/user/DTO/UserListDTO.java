package com.OjSystem.system.domain.user.DTO;

import com.OjSystem.common.core.domain.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserListDTO extends PageParam {
    private Long userId;

    private String nickName;
}
