package com.OjSystem.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import com.OjSystem.friend.Enum.exam.ExamListType;
import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.DTO.ExamRankDTO;
import com.OjSystem.friend.domain.exam.Exam;
import com.OjSystem.friend.domain.exam.ExamQuestion;
import com.OjSystem.friend.domain.exam.RankInfo;
import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.OjSystem.friend.domain.user.UserExam;
import com.OjSystem.friend.mapper.exam.ExamMapper;
import com.OjSystem.friend.mapper.exam.ExamQuestionMapper;
import com.OjSystem.friend.mapper.user.UserExamMapper;
import com.OjSystem.friend.service.exam.IExamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ExamCacheManager {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserExamMapper userExamMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    /**
     * 获取指定链条上的竞赛个数（根据用户id）
     *
     * @param examListType
     * @param userId
     * @return
     */
    public Long getListSize(Integer examListType, Long userId) {
        String examListKey = getExamListKey(examListType, userId);
        return redisService.getListSize(examListKey);
    }


    /**
     * 获取某个竞赛的题目数目
     *
     * @param examId
     * @return
     */
    public Long getQuestionListSize(Long examId) {
        String questionKey = getQuestionListKey(examId);
        return redisService.getListSize(questionKey);
    }


//    public Long getExamQuestionListSize(Long examId) {
//        String examQuestionListKey = getExamQuestionListKey(examId);
//        return redisService.getListSize(examQuestionListKey);
//    }
//
//    public Long getRankListSize(Long examId) {
//        return redisService.getListSize(getExamRankListKey(examId));
//    }


    /**
     * 查询指定链条上的数据
     *
     * @param examListDTO
     * @param userId
     * @return
     */
    public List<ExamVO> getExamVOList(ExamListDTO examListDTO, Long userId) {
        // 获取查询位置
        int start = (examListDTO.getPageNum() - 1) * examListDTO.getPageSize();
        int end = start + examListDTO.getPageSize() - 1; //下标需要 -1

        // 获取查询那条链
        String examListKey = getExamListKey(examListDTO.getType(), userId);

        List<Long> examIdList = redisService.getCacheListByRange(examListKey, start, end, Long.class);

        // 根据id查出竞赛数据
        List<ExamVO> examVOList = assembleExamVOList(examIdList);

        // 中间查询出现了某个key对应的value为null
        if (CollectionUtil.isEmpty(examVOList)) {
            //说明redis中数据可能有问题 从数据库中查数据并且重新刷新缓存
            examVOList = getExamListByDB(examListDTO, userId);
            refreshCache(examListDTO.getType(), userId);
        }

        return examVOList;
    }

//    public List<ExamRankVO> getExamRankList(ExamRankDTO examRankDTO) {
//        int start = (examRankDTO.getPageNum() - 1) * examRankDTO.getPageSize();
//        int end = start + examRankDTO.getPageSize() - 1; //下标需要 -1
//        return redisService.getCacheListByRange(getExamRankListKey(examRankDTO.getExamId()), start, end, ExamRankVO.class);
//    }
//
//    public List<Long> getAllUserExamList(Long userId) {
//        String examListKey = CacheConstants.USER_EXAM_LIST + userId;
//        List<Long> userExamIdList = redisService.getCacheListByRange(examListKey, 0, -1, Long.class);
//        if (CollectionUtil.isNotEmpty(userExamIdList)) {
//            return userExamIdList;
//        }
//        List<UserExam> userExamList =
//                userExamMapper.selectList(new LambdaQueryWrapper<UserExam>().eq(UserExam::getUserId, userId));
//        if (CollectionUtil.isEmpty(userExamList)) {
//            return null;
//        }
//        refreshCache(ExamListType.USER_EXAM_LIST.getValue(), userId);
//        return userExamList.stream().map(UserExam::getExamId).collect(Collectors.toList());
//    }

    /**
     * 给用户的竞赛列表链上添加数据
     *
     * @param userId
     * @param examId
     */
    public void addUserExamCache(Long userId, Long examId) {
        String userExamListKey = getUserExamListKey(userId);
        redisService.leftPushForList(userExamListKey, examId);
    }


    /**
     * 获取用户竞赛列表的key
     *
     * @param userId
     * @return
     */
    private String getUserExamListKey(Long userId) {
        return RedisConstants.USER_EXAM_LIST + userId;
    }


//
//    public Long getFirstQuestion(Long examId) {
//        return redisService.indexForList(getExamQuestionListKey(examId), 0, Long.class);
//    }
//
//    public Long preQuestion(Long examId, Long questionId) {
//        Long index = redisService.indexOfForList(getExamQuestionListKey(examId), questionId);
//        if (index == 0) {
//            throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
//        }
//        return redisService.indexForList(getExamQuestionListKey(examId), index - 1, Long.class);
//    }
//
//    public Long nextQuestion(Long examId, Long questionId) {
//        Long index = redisService.indexOfForList(getExamQuestionListKey(examId), questionId);
//        long lastIndex = getExamQuestionListSize(examId) - 1;
//        if (index == lastIndex) {
//            throw new ServiceException(ResultCode.FAILED_LAST_QUESTION);
//        }
//        return redisService.indexForList(getExamQuestionListKey(examId), index + 1, Long.class);
//    }


    /**
     * 给链条上的添加数据 + 存储单个竞赛数据
     *
     * @param examListType
     * @param userId
     */
    public void refreshCache(Integer examListType, Long userId) {
        List<Exam> examList = new ArrayList<>();
        if (ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)) {
            //查询未完赛的竞赛列表
            examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                    .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                    .gt(Exam::getEndTime, LocalDateTime.now())
                    .eq(Exam::getStatus, 1)
                    .orderByDesc(Exam::getCreateTime));
        } else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
            //查询历史竞赛
            examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                    .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                    .le(Exam::getEndTime, LocalDateTime.now())
                    .eq(Exam::getStatus, 1)
                    .orderByDesc(Exam::getCreateTime));
        } else if (ExamListType.USER_EXAM_LIST.getValue().equals(examListType)) {
            List<ExamVO> examVOS = userExamMapper.getExamList(userId);
            examList = examVOS.stream().map(examVO -> {
                Exam exam = new Exam();
                BeanUtils.copyProperties(examVO, exam);
                exam.setStatus(1);
                return exam;
            }).collect(Collectors.toList());
        }

        if (CollectionUtil.isEmpty(examList)) {
            return;
        }

        // 单个竞赛数据
        Map<String, Exam> examMap = new HashMap<>();
        // 链上数据
        List<Long> examIdList = new ArrayList<>();

        for (Exam exam : examList) {
            examMap.put(getDetailKey(exam.getExamId()), exam);
            examIdList.add(exam.getExamId());
        }
        redisService.multiSet(examMap);  //刷新详情缓存
        redisService.deleteObject(getExamListKey(examListType, userId));
        redisService.rightPushAll(getExamListKey(examListType, userId), examIdList);      //刷新列表缓存
    }

//    public void refreshExamQuestionCache(Long examId) {
//        List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
//                .select(ExamQuestion::getQuestionId)
//                .eq(ExamQuestion::getExamId, examId)
//                .orderByAsc(ExamQuestion::getQuestionOrder));
//        if (CollectionUtil.isEmpty(examQuestionList)) {
//            return;
//        }
//        List<Long> examQuestionIdList = examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();
//        redisService.rightPushAll(getExamQuestionListKey(examId), examQuestionIdList);
//        //节省 redis缓存资源
//        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
//                LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
//        redisService.expire(getExamQuestionListKey(examId), seconds, TimeUnit.SECONDS);
//    }
//
//    public void refreshExamRankCache(Long examId) {
//        List<ExamRankVO> examRankVOList = userExamMapper.selectExamRankList(examId);
//        if (CollectionUtil.isEmpty(examRankVOList)) {
//            return;
//        }
//        redisService.rightPushAll(getExamRankListKey(examId), examRankVOList);
//    }


    /**
     * 从数据库中查数据
     *
     * @param examListDTO
     * @param userId
     * @return
     */
    private List<ExamVO> getExamListByDB(ExamListDTO examListDTO, Long userId) {
        PageHelper.startPage(examListDTO.getPageNum(), examListDTO.getPageSize());
        if (ExamListType.USER_EXAM_LIST.getValue().equals(examListDTO.getType())) {
            return userExamMapper.getExamList(userId);
        } else {
            return examMapper.getExamList(examListDTO);
        }
    }


    /**
     * 根据id组获取竞赛数据
     *
     * @param examIdList
     * @return
     */
    private List<ExamVO> assembleExamVOList(List<Long> examIdList) {
        if (CollectionUtil.isEmpty(examIdList)) {
            return null;
        }

        // 获取每个id对应的key
        List<String> detailKeyList = new ArrayList<>();
        for (Long examId : examIdList) {
            detailKeyList.add(getDetailKey(examId));
        }

        List<ExamVO> examVOList = redisService.multiGet(detailKeyList, ExamVO.class);

        // 在极端情况下可能会出现某个key查出来为null
        CollUtil.removeNull(examVOList);

        //说明redis中数据有问题 从数据库中查数据并且重新刷新缓存
        if (CollectionUtil.isEmpty(examVOList) || examVOList.size() != examIdList.size()) {
            return null;
        }

        return examVOList;
    }


    /**
     * 获取指定链条的key
     *
     * @param examListType
     * @param userId
     * @return
     */
    private String getExamListKey(Integer examListType, Long userId) {
        if (ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)) {
            return RedisConstants.EXAM_UNFINISHED_LIST;
        } else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
            return RedisConstants.EXAM_HISTORY_LIST;
        } else {
            return RedisConstants.USER_EXAM_LIST + userId;
        }
    }


    /**
     * 获取竞赛详细信息key
     *
     * @param examId
     * @return
     */
    private String getDetailKey(Long examId) {
        return RedisConstants.EXAM_DETAIL + examId;
    }


    /**
     * 获取竞赛题目列表key
     *
     * @param examId
     * @return
     */
    private String getQuestionListKey(Long examId) {
        return RedisConstants.EXAM_QUESTION_LIST + examId;
    }


    /**
     * 获取指定用户所有报名的竞赛
     *
     * @param userId
     * @return
     */
    public List<Long> getAllUserExamList(Long userId) {
        String userKey = RedisConstants.USER_EXAM_LIST + userId;

        List<Long> listByRange = redisService.getCacheListByRange(userKey, 0, -1, Long.class);
        if (!CollectionUtils.isEmpty(listByRange)) {
            return listByRange;
        }

        LambdaQueryWrapper<UserExam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserExam::getUserId, userId);
        List<UserExam> userExams = userExamMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(userExams)) {
            return null;
        }

        return userExams.stream().map(UserExam::getExamId).collect(Collectors.toList());
    }


    /**
     * 刷新竞赛题目列表redis
     */
    public void refreshExamQuestion(Long examId) {
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId).orderByAsc(ExamQuestion::getQuestionOrder));
        if (examQuestions == null) {
            return;
        }

        List<Long> questionIds = examQuestions.stream().map(examQuestion -> examQuestion.getQuestionId()).collect(Collectors.toList());
        redisService.rightPushAll(getQuestionListKey(examId), questionIds);

        // 设置过期时间
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        redisService.expire(getQuestionListKey(examId), seconds, TimeUnit.SECONDS);
    }


    /**
     * 获取竞赛的第一道题目id
     *
     * @param examId
     * @return
     */
    public Long getQuestionOfFirst(Long examId) {
        return redisService.index(getQuestionListKey(examId), 0, Long.class);
    }


    /**
     * 获取竞赛的上一道题目id
     *
     * @param questionId
     * @return
     */
    public Long preQuestion(Long examId, Long questionId) {
        Long index = redisService.indexOf(getQuestionListKey(examId), questionId);
        if (index == 0) {
            throw new FriendServiceException(FriendServiceErrorCode.THIS_IS_FIRST_QUESTION);
        }
        return redisService.index(getQuestionListKey(examId), index - 1, Long.class);
    }


    /**
     * 获取竞赛的下一道题目id
     *
     * @param examId
     * @param questionId
     * @return
     */
    public Long nextQuestion(Long examId, Long questionId) {
        Long index = redisService.indexOf(getQuestionListKey(examId), questionId);
        Long questionListSize = getQuestionListSize(examId);
        if (index == questionListSize - 1) {
            throw new FriendServiceException(FriendServiceErrorCode.THIS_IS_LAST_QUESTION);
        }
        return redisService.index(getQuestionListKey(examId), index + 1, Long.class);
    }


    /**
     * 获取竞赛排名列表数据个数
     *
     * @param examId
     * @return
     */
    public Long getRankListSize(Long examId) {
        return redisService.getListSize(getExamRankListKey(examId));
    }


    /**
     * 获取竞赛排名列表key
     *
     * @param examId
     * @return
     */
    private String getExamRankListKey(Long examId) {
        return RedisConstants.EXAM_RANK_LIST + examId;
    }


    /**
     * 刷新竞赛排名redis链
     *
     * @param examId
     */
    public void refreshExamRankCache(Long examId) {
        List<RankInfo> examRankVOList = examMapper.selectExamRankList(examId);
        if (CollectionUtil.isEmpty(examRankVOList)) {
            return;
        }
        redisService.rightPushAll(getExamRankListKey(examId), examRankVOList);
    }


    /**
     * 从竞赛排名列表redis链上获取数据
     *
     * @param examRankDTO
     * @return
     */
    public List<RankInfo> getExamRankList(ExamRankDTO examRankDTO) {
        int start = (examRankDTO.getPageNum() - 1) * examRankDTO.getPageSize();
        int end = start + examRankDTO.getPageSize() - 1; //下标需要 -1

        return redisService.getCacheListByRange(getExamRankListKey(examRankDTO.getExamId()), start, end, RankInfo.class);
    }
}
