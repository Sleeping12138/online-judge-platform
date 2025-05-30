package com.OjSystem.friend.service.user.impl;

import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.core.utils.ThreadLocalUtil;
import com.OjSystem.common.security.service.TokenService;
import com.OjSystem.friend.Enum.exam.ExamListType;
import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.Exam;
import com.OjSystem.friend.domain.exam.VO.ExamListVO;
import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.OjSystem.friend.domain.user.UserExam;
import com.OjSystem.friend.mapper.exam.ExamMapper;
import com.OjSystem.friend.mapper.user.UserExamMapper;
import com.OjSystem.friend.service.user.IUserExamService;
import com.OjSystem.manager.ExamCacheManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserExamServiceImpl implements IUserExamService {

    @Autowired
    private UserExamMapper userExamMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ExamCacheManager examCacheManager;


    /**
     * 报名竞赛
     *
     * @param token
     * @param examId
     * @return
     */
    @Override
    public boolean enter(String token, Long examId) {
        // 校验数据
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new FriendServiceException(FriendServiceErrorCode.EXAM_IS_NOT_EXIST);
        }
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new FriendServiceException(FriendServiceErrorCode.EXAM_HAS_START);
        }

        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        LambdaQueryWrapper<UserExam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserExam::getExamId, examId).eq(UserExam::getUserId, userId);
        UserExam userExam = userExamMapper.selectOne(queryWrapper);
        if (userExam != null) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_HAS_ENTER_EXAM);
        }


        // 存储数据到redis中
        examCacheManager.addUserExamCache(userId, examId);


        // 存储数据到数据库中
        UserExam newUserExam = new UserExam();
        newUserExam.setExamId(examId);
        newUserExam.setUserId(userId);

        return userExamMapper.insert(newUserExam) > 0;
    }


    /**
     * 获取用户报名竞赛列表
     * @param examListDTO
     * @return
     */
    @Override
    public ExamListVO<ExamVO> getUserExamList(ExamListDTO examListDTO) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);

        // 查看指定链上的元素个数
        Long total = examCacheManager.getListSize(ExamListType.USER_EXAM_LIST.getValue(), userId);

        List<ExamVO> examVOList = null;
        if (total == null || total == 0) {
            // 该链条上没有数据，从数据库中查询
            PageHelper.startPage(examListDTO.getPageNum(),examListDTO.getPageSize());
            examVOList = userExamMapper.getExamList(userId);

            // 给链条上的添加数据 + 存储单个竞赛数据
            examCacheManager.refreshCache(ExamListType.USER_EXAM_LIST.getValue(),null);
            total = new PageInfo<>(examVOList).getTotal();
        }else{
            // 从指定链条上获取竞赛数据
            examListDTO.setType(ExamListType.USER_EXAM_LIST.getValue());
            examVOList = examCacheManager.getExamVOList(examListDTO,null);
            total = examCacheManager.getListSize(examListDTO.getType(), null);
        }

        // 封装数据
        ExamListVO<ExamVO> examListVO = new ExamListVO<>();
        examListVO.setTotal(total);
        examListVO.setDataList(examVOList);
        return examListVO;
    }
}
