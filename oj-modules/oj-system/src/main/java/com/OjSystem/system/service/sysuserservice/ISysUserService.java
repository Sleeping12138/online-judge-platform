package com.OjSystem.system.service.sysuserservice;

import com.OjSystem.common.core.result.CommonResult;
import com.OjSystem.system.domain.sysuser.AdminUser;
import com.OjSystem.system.domain.sysuser.DTO.LoginDTO;
import com.OjSystem.system.domain.sysuser.VO.LoginUserVO;

public interface ISysUserService {
    String login(LoginDTO loginDTO);

    boolean addAdminUser(AdminUser adminUser);

    LoginUserVO getLoginUserInfo(String token);

    boolean logout(String token);
}
