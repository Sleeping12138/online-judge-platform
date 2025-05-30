package com.OjSystem.friend.service.user;

import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.VO.ExamListVO;
import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.OjSystem.friend.domain.user.DTO.HeadImageUpdateDTO;
import com.OjSystem.friend.domain.user.DTO.UserMessageDTO;
import com.OjSystem.friend.domain.user.DTO.UserUpdateDTO;
import com.OjSystem.friend.domain.user.VO.LoginUserVO;
import com.OjSystem.friend.domain.user.VO.UserVO;

import java.util.List;

public interface IUserService {
    boolean sendMessage(UserMessageDTO userMessageDTO);

    String login(UserMessageDTO userMessageDTO);

    boolean logout(String token);

    LoginUserVO getLoginUserInfo(String token);

    UserVO getUserDetailInfo();

    boolean editUserInfo(UserUpdateDTO userUpdateDTO);

    boolean headImageUpdate(HeadImageUpdateDTO headImageUpdateDTO);
}
