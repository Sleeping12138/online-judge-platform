package com.OjSystem.friend.service.exam.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.core.utils.ThreadLocalUtil;
import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.DTO.ExamRankDTO;
import com.OjSystem.friend.domain.exam.RankInfo;
import com.OjSystem.friend.domain.exam.VO.ExamListVO;
import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.OjSystem.friend.mapper.exam.ExamMapper;
import com.OjSystem.friend.service.exam.IExamService;
import com.OjSystem.manager.ExamCacheManager;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamCacheManager examCacheManager;


    /**
     * 获取竞赛列表（从数据库获取竞赛列表）
     *
     * @param examListDTO
     * @return
     */
    @Override
    public List<ExamVO> getExamList(ExamListDTO examListDTO) {
        return examMapper.getExamList(examListDTO);
    }


    /**
     * 获取竞赛列表（从redis中获取竞赛列表）
     *
     * @param examListDTO
     * @return
     */
    @Override
    public ExamListVO<ExamVO> getExamListFromRedis(ExamListDTO examListDTO) {
        // 查看指定链上的元素个数
        Long total = examCacheManager.getListSize(examListDTO.getType(), null);

        List<ExamVO> examVOList = null;
        if (total == null || total == 0) {
            // 该链条上没有数据，从数据库中查询
            examVOList = getExamList(examListDTO);
            // 给链条上的添加数据 + 存储单个竞赛数据
            examCacheManager.refreshCache(examListDTO.getType(), null);
            total = new PageInfo<>(examVOList).getTotal();
        } else {
            // 从指定链条上获取竞赛数据
            examVOList = examCacheManager.getExamVOList(examListDTO, null);
            total = examCacheManager.getListSize(examListDTO.getType(), null);
        }

        // 修改用户是否报名信息
        assembleExamVOList(examVOList);

        // 封装数据
        ExamListVO<ExamVO> examListVO = new ExamListVO<>();
        examListVO.setTotal(total);
        examListVO.setDataList(examVOList);
        return examListVO;
    }


    /**
     * 修改用户是否报名信息
     *
     * @param examVOList
     */
    public void assembleExamVOList(List<ExamVO> examVOList) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        List<Long> userExamList = examCacheManager.getAllUserExamList(userId);
        if (CollectionUtils.isEmpty(userExamList)) {
            return;
        }
        for (ExamVO examVO : examVOList) {
            if (userExamList.contains(examVO.getExamId())) {
                examVO.setEnter(true);
            }
        }
    }


    /**
     * 获取竞赛的第一道题目
     *
     * @param examId
     * @return
     */
    @Override
    public String getQuestionOfFirst(Long examId) {
        Long questionListSize = examCacheManager.getQuestionListSize(examId);
        if (questionListSize == null || questionListSize <= 0) {
            examCacheManager.refreshExamQuestion(examId);
        }

        return examCacheManager.getQuestionOfFirst(examId).toString();
    }


    /**
     * 获取竞赛题目上一题
     *
     * @param examId
     * @param questionId
     * @return
     */
    @Override
    public String preQuestion(Long examId, Long questionId) {
        Long questionListSize = examCacheManager.getQuestionListSize(examId);
        if (questionListSize == null || questionListSize <= 0) {
            examCacheManager.refreshExamQuestion(examId);
        }

        return examCacheManager.preQuestion(examId, questionId).toString();
    }


    /**
     * 获取竞赛题目下一题
     *
     * @param examId
     * @param questionId
     * @return
     */
    @Override
    public String nextQuestion(Long examId, Long questionId) {
        Long questionListSize = examCacheManager.getQuestionListSize(examId);
        if (questionListSize == null || questionListSize <= 0) {
            examCacheManager.refreshExamQuestion(examId);
        }

        return examCacheManager.nextQuestion(examId, questionId).toString();
    }


    /**
     * 获取竞赛排名数据
     *
     * @param examRankDTO
     * @return
     */
    @Override
    public ExamListVO<RankInfo> getExamRankInfo(ExamRankDTO examRankDTO) {
        // 获取竞赛链数据个数
        Long total = examCacheManager.getRankListSize(examRankDTO.getExamId());

        List<RankInfo> rankInfos;
        if (total == null || total <= 0) {

            // 从数据库中查
            PageHelper.startPage(examRankDTO.getPageNum(), examRankDTO.getPageSize());
            rankInfos = examMapper.selectExamRankList(examRankDTO.getExamId());

            // 刷新redis链
            examCacheManager.refreshExamRankCache(examRankDTO.getExamId());
            total = new PageInfo<>(rankInfos).getTotal();
        } else {

            // 从redis链上进行获取
            rankInfos = examCacheManager.getExamRankList(examRankDTO);
        }

        ExamListVO<RankInfo> rankInfoExamListVO = new ExamListVO<>();

        if (CollectionUtil.isEmpty(rankInfos)) {
            return rankInfoExamListVO;
        }

        rankInfoExamListVO.setTotal(total);
        rankInfoExamListVO.setDataList(rankInfos);
        return rankInfoExamListVO;
    }

}
