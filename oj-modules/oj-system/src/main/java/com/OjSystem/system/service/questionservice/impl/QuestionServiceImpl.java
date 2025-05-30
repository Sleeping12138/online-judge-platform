package com.OjSystem.system.service.questionservice.impl;

import com.OjSystem.common.core.errorcode.modules.ojsystem.SystemServiceErrorCode;
import com.OjSystem.common.core.exception.sysuser.SystemServiceException;
import com.OjSystem.system.domain.question.DO.QuestionDO;
import com.OjSystem.system.domain.question.DTO.AddQuestionDTO;
import com.OjSystem.system.domain.question.DTO.ChangeQuestionDTO;
import com.OjSystem.system.domain.question.DTO.QuestionDTO;
import com.OjSystem.system.domain.question.Question;
import com.OjSystem.system.domain.question.VO.QuestionDetailVO;
import com.OjSystem.system.manager.QuestionCacheManager;
import com.OjSystem.system.mapper.questionmapper.QuestionMapper;
import com.OjSystem.system.service.questionservice.IQuestionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionCacheManager questionCacheManager;

    /**
     * 获取题目列表
     *
     * @param questionDTO
     * @return
     */
    @Override
    public List<QuestionDO> getQuestionList(QuestionDTO questionDTO) {
        if (StringUtils.hasText(questionDTO.getExcludeIdStr())) {
            String[] split = questionDTO.getExcludeIdStr().split(";");
            Set<Long> idSet = Arrays.stream(split).map(Long::valueOf).collect(Collectors.toSet());
            questionDTO.setExcludeIdSet(idSet);
        }
        return questionMapper.getQuestionList(questionDTO);
    }


    /**
     * 添加题目
     *
     * @param addQuestionDTO
     * @return
     */
    @Override
    public boolean addQuestion(AddQuestionDTO addQuestionDTO) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", addQuestionDTO.getTitle());
        List<Question> questions = questionMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(questions)) {
            throw new SystemServiceException(SystemServiceErrorCode.QUESTION_HAS_EXIST);
        }

        Question question = new Question();
        BeanUtils.copyProperties(addQuestionDTO, question);

        // 更新redis
        questionCacheManager.addElementToList(question.getQuestionId());

        return questionMapper.insert(question) > 0;
    }

    /**
     * 获取指定的题目信息
     *
     * @param questionId
     * @return
     */
    @Override
    public QuestionDetailVO getDetailById(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new SystemServiceException(SystemServiceErrorCode.QUESTION_IS_NOT_EXIST);
        }

        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        BeanUtils.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }


    /**
     * 修改题目信息
     *
     * @param changeQuestionDTO
     * @return
     */
    @Override
    public boolean changeQuestionInfo(ChangeQuestionDTO changeQuestionDTO) {
        Question question = questionMapper.selectById(changeQuestionDTO.getQuestionId());
        if (question == null) {
            throw new SystemServiceException(SystemServiceErrorCode.QUESTION_IS_NOT_EXIST);
        }

        question.setTitle(changeQuestionDTO.getTitle());
        question.setDifficulty(changeQuestionDTO.getDifficulty());
        question.setTimeLimit(changeQuestionDTO.getTimeLimit());
        question.setSpaceLimit(changeQuestionDTO.getSpaceLimit());
        question.setContent(changeQuestionDTO.getContent());
        question.setQuestionCase(changeQuestionDTO.getQuestionCase());
        question.setDefaultCode(changeQuestionDTO.getDefaultCode());
        question.setMainFuc(changeQuestionDTO.getMainFuc());

        return questionMapper.updateById(question) > 0;
    }


    /**
     * 删除题目
     *
     * @param questionId
     * @return
     */
    @Override
    public boolean deleteQuestion(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new SystemServiceException(SystemServiceErrorCode.QUESTION_IS_NOT_EXIST);
        }

        // 更新redis
        questionCacheManager.removeElementFromList(questionId);

        return questionMapper.deleteById(questionId) > 0;
    }
}
