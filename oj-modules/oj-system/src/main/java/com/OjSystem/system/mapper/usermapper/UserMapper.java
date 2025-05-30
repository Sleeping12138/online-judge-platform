package com.OjSystem.system.mapper.usermapper;

import com.OjSystem.system.domain.user.DTO.UserListDTO;
import com.OjSystem.system.domain.user.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<User> getUserList(UserListDTO userListDTO);
}
