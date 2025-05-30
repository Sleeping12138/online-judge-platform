package com.OjSystem.friend.service.quesiton.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import com.OjSystem.friend.domain.exam.DTO.QuestionListDTO;
import com.OjSystem.friend.domain.exam.VO.QuestionListVO;
import com.OjSystem.friend.domain.exam.VO.QuestionVO;
import com.OjSystem.friend.domain.question.DTO.QuestionDetailVO;
import com.OjSystem.friend.domain.question.Question;
import com.OjSystem.friend.mapper.question.QuestionMapper;
import com.OjSystem.friend.service.quesiton.IQuestionService;
import com.OjSystem.manager.QuestionCacheManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements IQuestionService {

//    @Autowired
//    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionCacheManager questionCacheManager;

    @Autowired
    private RedisService redisService;

    /**
     * 获取题目列表
     *
     * @param questionListDTO
     * @return
     */
    @Override
    public QuestionListVO<QuestionVO> getQuestionList(QuestionListDTO questionListDTO) {
        // TODO 修改为当xxx不为空时

        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            refreshQuestion();
        }

        List<Long> questionList = redisService.getListOfKey(RedisConstants.QUESTION_LIST);

        List<Question> questions = questionMapper.selectByQuestionIds(questionList);

        List<QuestionVO> voList = questions.stream().map(question -> {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(question, questionVO);
            return questionVO;
        }).collect(Collectors.toList());

        QuestionListVO<QuestionVO> questionListVO = new QuestionListVO<>();
        questionListVO.setTotal(voList.size());
        questionListVO.setDataList(voList);

        return questionListVO;
    }


    // 这个才是真的
    /*public QuestionListVO<QuestionVO> getQuestionList2(QuestionListDTO questionListDTO) {
        // 查询elasticSearch中是否存在数据
        long count = questionRepository.count();
        if (count <= 0) {
            refreshQuestion();
        }

        // 提取查询数据
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(questionListDTO.getPageNum() - 1, questionListDTO.getPageSize(), sort);
        Integer difficulty = questionListDTO.getDifficulty();
        String keyword = questionListDTO.getKeyword();

        // 判断情况
        Page<QuestionES> pageList = null;
        if (difficulty == null && StrUtil.isEmpty(keyword)) {
            pageList = questionRepository.findAll(pageable);
        } else if (difficulty == null) {
            pageList = questionRepository.findByTitleOrContent(keyword, keyword, pageable);
        } else if (StrUtil.isEmpty(keyword)) {
            pageList = questionRepository.findQuestionByDifficulty(difficulty, pageable);
        } else {
            pageList = questionRepository.findByTitleOrContentAndDifficulty(keyword, keyword, difficulty, pageable);
        }

        // 获取total和列表
        long total = pageList.getTotalElements();
        List<QuestionES> content = pageList.getContent();
        List<QuestionVO> voList = content.stream().map(questionES -> {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(questionES, questionVO);
            return questionVO;
        }).collect(Collectors.toList());

        // 封装数据
        QuestionListVO<QuestionVO> questionListVO = new QuestionListVO<>();
        questionListVO.setTotal(total);
        questionListVO.setDataList(voList);

        return questionListVO;
    }*/


    /**
     * 将数据库中数据更新到elasticSearch中
     */
    /*private void refreshQuestion() {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<>());
        if (CollectionUtils.isEmpty(questions)) {
            return;
        }

        List<QuestionES> esList = questions.stream().map(question -> {
            QuestionES questionES = new QuestionES();
            BeanUtils.copyProperties(question, questionES);
            return questionES;
        }).collect(Collectors.toList());

        questionRepository.saveAll(esList);
    }*/


    /**
     * 获取竞赛详细信息
     *
     * @param questionId
     * @return
     */
    @Override
    public QuestionDetailVO getQuestionInfo(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return null;
        }
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        BeanUtils.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }


    // 这才是真的
    /*@Override
    public QuestionDetailVO detail(Long questionId) {
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        if (questionES != null) {
            BeanUtil.copyProperties(questionES, questionDetailVO);
            return questionDetailVO;
        }
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return null;
        }
        refreshQuestion();
        BeanUtil.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }*/


    /**
     * 获取上一题
     *
     * @param questionId
     * @return
     */
    @Override
    public String preQuestion(Long questionId) {
        Long size = questionCacheManager.getListSize();
        if (size == null || size <= 0) {
            refreshQuestion();
        }

        // 获取前一题的id
        return questionCacheManager.preQuestion(questionId).toString();
    }


    /**
     * 获取上一题
     *
     * @param questionId
     * @return
     */
    @Override
    public String nextQuestion(Long questionId) {
        Long size = questionCacheManager.getListSize();
        if (size == null || size <= 0) {
            refreshQuestion();
        }

        // 获取前一题的id
        return questionCacheManager.nextQuestion(questionId).toString();
    }


    /**
     * 刷新redis中的数据
     */
    private void refreshQuestion() {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId).orderByDesc(Question::getCreateTime));

        if (CollectionUtils.isEmpty(questions)) {
            return;
        }

        List<Long> questionIds = questions.stream().map(Question::getQuestionId).collect(Collectors.toList());
        redisService.rightPushAll(RedisConstants.QUESTION_LIST, questionIds);
    }
}
