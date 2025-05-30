package com.OjSystem.system.controller.user;

import com.OjSystem.system.domain.user.DTO.ChangeStatusDTO;
import com.OjSystem.system.domain.user.DTO.UserListDTO;
import com.OjSystem.system.domain.user.User;
import com.OjSystem.system.domain.user.VO.UserListVO;
import com.OjSystem.system.service.userservice.IUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private IUserService userService;


    /**
     * 获取用户列表
     *
     * @param userListDTO
     * @return
     */
    @RequestMapping("/getUserList")
    public UserListVO<User> getUserList(UserListDTO userListDTO) {
        PageHelper.startPage(userListDTO.getPageNum(), userListDTO.getPageSize());
        List<User> userList = userService.getUserList(userListDTO);

        long total = new PageInfo<>(userList).getTotal();
        UserListVO<User> userUserListVO = new UserListVO<>();
        userUserListVO.setTotal(total);
        userUserListVO.setDataList(userList);

        return userUserListVO;
    }


    /**
     * 修改用户状态
     * @param changeStatusDTO
     * @return
     */
    @RequestMapping("/changeStatus")
    public boolean changeStatus(@RequestBody ChangeStatusDTO changeStatusDTO){
        return userService.changeStatus(changeStatusDTO);
    }
}
