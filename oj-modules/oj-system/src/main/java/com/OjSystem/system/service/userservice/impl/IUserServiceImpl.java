package com.OjSystem.system.service.userservice.impl;

import com.OjSystem.common.core.errorcode.modules.ojsystem.SystemServiceErrorCode;
import com.OjSystem.common.core.exception.sysuser.SystemServiceException;
import com.OjSystem.system.domain.user.DTO.ChangeStatusDTO;
import com.OjSystem.system.domain.user.DTO.UserListDTO;
import com.OjSystem.system.domain.user.User;
import com.OjSystem.system.domain.user.VO.UserListVO;
import com.OjSystem.system.manager.UserCacheManager;
import com.OjSystem.system.mapper.usermapper.UserMapper;
import com.OjSystem.system.service.userservice.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCacheManager userCacheManager;

    /**
     * 获取用户列表
     *
     * @param userListDTO
     * @return
     */
    @Override
    public List<User> getUserList(UserListDTO userListDTO) {
        return userMapper.getUserList(userListDTO);
    }


    /**
     * 修改用户状态
     *
     * @param changeStatusDTO
     * @return
     */
    @Override
    public boolean changeStatus(ChangeStatusDTO changeStatusDTO) {
        User user = userMapper.selectById(changeStatusDTO.getUserId());
        if (user == null) {
            throw new SystemServiceException(SystemServiceErrorCode.USER_IS_NOT_EXIST);
        }

        user.setStatus(changeStatusDTO.getStatus());

        // 更新redis
        userCacheManager.changeUserStatus(user.getUserId(),changeStatusDTO.getStatus());

        // 更新数据库
        return userMapper.updateById(user) > 0;
    }
}
