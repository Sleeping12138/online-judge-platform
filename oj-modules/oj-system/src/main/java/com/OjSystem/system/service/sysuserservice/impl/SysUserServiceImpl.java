package com.OjSystem.system.service.sysuserservice.impl;

import com.OjSystem.common.core.errorcode.modules.ojsystem.SystemServiceErrorCode;
import com.OjSystem.common.core.exception.sysuser.SystemServiceException;
import com.OjSystem.common.core.result.CommonResult;
import com.OjSystem.common.security.Enum.LoginUserEnum;
import com.OjSystem.common.security.domain.LoginUser;
import com.OjSystem.common.security.service.TokenService;
import com.OjSystem.system.domain.sysuser.AdminUser;
import com.OjSystem.system.domain.sysuser.DTO.LoginDTO;
import com.OjSystem.system.domain.sysuser.VO.LoginUserVO;
import com.OjSystem.system.domain.sysuser.SysUser;
import com.OjSystem.system.mapper.sysusermapper.SysUserMapper;
import com.OjSystem.system.service.sysuserservice.ISysUserService;
import com.OjSystem.system.utils.BCryptUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private TokenService tokenService;

    /**
     * 管理员登录
     *
     * @param loginDTO
     * @return
     */
    @Override
    public String login(LoginDTO loginDTO) {
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.eq("user_account", loginDTO.getUserAccount());
        SysUser sysUser = sysUserMapper.selectOne(sysUserQueryWrapper);

        if (sysUser == null) {
            throw new SystemServiceException(SystemServiceErrorCode.SYSUSER_IS_NOT_EXIST);
        }

        if (!BCryptUtils.matchesPassword(loginDTO.getPassword(), sysUser.getPassword())) {
            throw new SystemServiceException(SystemServiceErrorCode.PASSWORD_IS_ERROR);
        }

        // 获取token
        String token = tokenService.createToken(sysUser.getUserId(), LoginUserEnum.ADMIN.getCode(),sysUser.getNickName(),null);

        return token;
    }

    /**
     * 添加管理员接口
     *
     * @param adminUser
     * @return
     */
    @Override
    public boolean addAdminUser(AdminUser adminUser) {
        // 判断添加的管理员是否已经存在
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.eq("user_account", adminUser.getUserAccount());
        List<SysUser> sysUsers = sysUserMapper.selectList(sysUserQueryWrapper);
        if (!CollectionUtils.isEmpty(sysUsers)) {
            log.error(SystemServiceErrorCode.ADMIN_USER_HAS_EXIST.getErrMsg());
            return false;
        }

        // 添加管理员
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(adminUser.getUserAccount());
        sysUser.setPassword(BCryptUtils.encryptPassword(adminUser.getPassword()));
        sysUser.setNickName("admin");

        return sysUserMapper.insert(sysUser) > 0;
    }

    /**
     * 获得登录者的昵称
     * @param token
     * @return
     */
    @Override
    public LoginUserVO getLoginUserInfo(String token) {
        LoginUser loginUser = tokenService.getLoginUserInfoFromToken(token);
        if (loginUser == null) {
            return null;
        }

        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());

        return loginUserVO;
    }

    /**
     * 退出登录
     * @param token
     * @return
     */
    @Override
    public boolean logout(String token) {
        return tokenService.deleteLoginUserToken(token);
    }
}
