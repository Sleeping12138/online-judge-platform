package com.OjSystem.friend.controller.user;

import com.OjSystem.aspect.CheckUserStatus;
import com.OjSystem.common.core.constants.HttpConstants;
import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.VO.ExamListVO;
import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.OjSystem.friend.domain.exam.DTO.EnterDTO;
import com.OjSystem.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/exam")
@RestController
public class UserExamController {

    @Autowired
    private IUserExamService userExamService;


    /**
     * 报名竞赛
     *
     * @param token
     * @param enterDTO
     * @return
     */
    @CheckUserStatus
    @RequestMapping("/enter")
    public boolean enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody EnterDTO enterDTO) {
        return userExamService.enter(token, enterDTO.getExamId());
    }


    /**
     * 获取用户报名竞赛列表
     * @param examListDTO
     * @return
     */
    @RequestMapping("/getUserExamList")
    public ExamListVO<ExamVO> getUserExamList(ExamListDTO examListDTO) {
        return userExamService.getUserExamList(examListDTO);
    }
}
