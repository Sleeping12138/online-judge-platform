package com.OjSystem.system.service.userservice;

import com.OjSystem.system.domain.user.DTO.ChangeStatusDTO;
import com.OjSystem.system.domain.user.DTO.UserListDTO;
import com.OjSystem.system.domain.user.User;
import com.OjSystem.system.domain.user.VO.UserListVO;

import java.util.List;

public interface IUserService {
    List<User> getUserList(UserListDTO userListDTO);

    boolean changeStatus(ChangeStatusDTO changeStatusDTO);
}
