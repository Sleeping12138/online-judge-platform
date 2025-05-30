package com.OjSystem.friend.controller.user;

import com.OjSystem.common.core.constants.HttpConstants;
import com.OjSystem.friend.domain.user.DTO.HeadImageUpdateDTO;
import com.OjSystem.friend.domain.user.DTO.UserMessageDTO;
import com.OjSystem.friend.domain.user.DTO.UserUpdateDTO;
import com.OjSystem.friend.domain.user.VO.LoginUserVO;
import com.OjSystem.friend.domain.user.VO.UserVO;
import com.OjSystem.friend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 发送短信验证码
     *
     * @param userMessageDTO
     * @return
     */
    @RequestMapping("/sendMessage")
    public boolean sendMessage(@RequestBody UserMessageDTO userMessageDTO) {
        return userService.sendMessage(userMessageDTO);
    }


    /**
     * 用户登录
     *
     * @param userMessageDTO
     * @return
     */
    @RequestMapping("/login")
    public String login(@RequestBody UserMessageDTO userMessageDTO) {
        return userService.login(userMessageDTO);
    }


    /**
     * 退出登录
     *
     * @param token
     * @return
     */
    @RequestMapping("/logout")
    public boolean logout(@RequestHeader(value = HttpConstants.AUTHENTICATION) String token) {
        return userService.logout(token);
    }


    /**
     * 获得登录者的信息
     *
     * @param token
     * @return
     */
    @RequestMapping("/getLoginUserInfo")
    public LoginUserVO getLoginUserInfo(@RequestHeader(value = HttpConstants.AUTHENTICATION) String token) {
        return userService.getLoginUserInfo(token);
    }


    /**
     * 获取用户详情信息
     *
     * @return
     */
    @RequestMapping("/getUserDetailInfo")
    public UserVO getUserDetailInfo() {
        return userService.getUserDetailInfo();
    }


    /**
     * 更新用户信息数据
     *
     * @param userUpdateDTO
     * @return
     */
    @RequestMapping("/editUserInfo")
    public boolean editUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.editUserInfo(userUpdateDTO);
    }


    /**
     * 更新数据库中关于头像字段
     * @param headImageUpdateDTO
     * @return
     */
    @RequestMapping("/head-image/update")
    public boolean headImageUpdate(@RequestBody HeadImageUpdateDTO headImageUpdateDTO){
        return userService.headImageUpdate(headImageUpdateDTO);
    }
}
