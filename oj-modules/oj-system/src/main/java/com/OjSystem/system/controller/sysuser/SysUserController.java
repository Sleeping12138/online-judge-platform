package com.OjSystem.system.controller.sysuser;

import com.OjSystem.common.core.constants.HttpConstants;
import com.OjSystem.common.core.result.CommonResult;
import com.OjSystem.system.domain.sysuser.AdminUser;
import com.OjSystem.system.domain.sysuser.DTO.LoginDTO;
import com.OjSystem.system.domain.sysuser.VO.LoginUserVO;
import com.OjSystem.system.service.sysuserservice.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sysUser")
@Tag(name = "管理员用户模块")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 测试接口
     *
     * @return
     */
    @RequestMapping("/test")
    public String test() {
        return "hello world";
    }

    /**
     * 添加管理员接口
     *
     * @param adminUser
     * @return
     */
    @RequestMapping("/add")
    public boolean addAdminUser(@RequestBody @Validated AdminUser adminUser) {
        return sysUserService.addAdminUser(adminUser);
    }


    /**
     * 管理员登录
     *
     * @param loginDTO
     * @return
     */
    @Operation(summary = "登录", description = "使用账号和密码进行登录")
    @Parameters(value = {
            @Parameter(name = "userAccount", description = "传递的账号"),
            @Parameter(name = "password", description = "传递的密码"),
    })
    @ApiResponse(description = "返回token")
    @PostMapping("/login")
    public String login(@RequestBody @Validated LoginDTO loginDTO) {
        return sysUserService.login(loginDTO);
    }

    /**
     * 获得登录者的昵称
     * @param token
     * @return
     */
    @RequestMapping("/getLoginUserInfo")
    public LoginUserVO getLoginUserInfo(@RequestHeader(value = HttpConstants.AUTHENTICATION) String token) {
        return sysUserService.getLoginUserInfo(token);
    }

    /**
     * 退出登录
     * @param token
     * @return
     */
    @RequestMapping("/logout")
    public boolean logout(@RequestHeader(value = HttpConstants.AUTHENTICATION) String token) {
        return sysUserService.logout(token);
    }
}
