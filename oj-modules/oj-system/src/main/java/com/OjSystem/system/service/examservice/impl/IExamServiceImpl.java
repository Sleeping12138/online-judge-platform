package com.OjSystem.system.service.examservice.impl;

import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.core.errorcode.modules.ojsystem.SystemServiceErrorCode;
import com.OjSystem.common.core.exception.sysuser.SystemServiceException;
import com.OjSystem.system.controller.Enum.exam.ExamEnum;
import com.OjSystem.system.domain.exam.DTO.AddExamQuestionDTO;
import com.OjSystem.system.domain.exam.DTO.EditExamDTO;
import com.OjSystem.system.domain.exam.DTO.ExamAddDTO;
import com.OjSystem.system.domain.exam.DTO.ExamGetListDTO;
import com.OjSystem.system.domain.exam.Exam;
import com.OjSystem.system.domain.exam.ExamQuestion;
import com.OjSystem.system.domain.exam.VO.ExamDetailVO;
import com.OjSystem.system.domain.exam.VO.ExamVO;
import com.OjSystem.system.domain.question.DO.QuestionDO;
import com.OjSystem.system.domain.question.Question;
import com.OjSystem.system.manager.ExamCacheManager;
import com.OjSystem.system.mapper.exammapper.ExamMapper;
import com.OjSystem.system.mapper.exammapper.ExamQuestionMapper;
import com.OjSystem.system.mapper.questionmapper.QuestionMapper;
import com.OjSystem.system.service.examservice.IExamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IExamServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements IExamService {


    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private ExamCacheManager examCacheManager;


    /**
     * 校验竞赛是否存在
     *
     * @param examId
     */
    private Exam checkExamIsExist(Long examId) {
        // 校验数据
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_IS_NOT_EXIST);
        }
        return exam;
    }


    /**
     * 返回竞赛列表
     *
     * @param examGetListDTO
     * @return
     */
    @Override
    public List<ExamVO> getExamList(ExamGetListDTO examGetListDTO) {
        return examMapper.getExamList(examGetListDTO);
    }


    /**
     * 添加竞赛
     *
     * @param examAddDTO
     * @return
     */
    @Override
    public String addExam(ExamAddDTO examAddDTO) {
        // 参数校验
        QueryWrapper<Exam> examQueryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<Exam> queryWrapper = examQueryWrapper.lambda().eq(Exam::getTitle, examAddDTO.getTitle());
        List<Exam> exams = examMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(exams)) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_HAS_EXIST);
        }

        if (examAddDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new SystemServiceException(SystemServiceErrorCode.START_TIME_IS_ERROR);
        }

        if (examAddDTO.getStartTime().isAfter(examAddDTO.getEndTime())) {
            throw new SystemServiceException(SystemServiceErrorCode.END_TIME_IS_ERROR);
        }

        // 保存数据
        Exam exam = new Exam();
        exam.setTitle(examAddDTO.getTitle());
        exam.setStartTime(examAddDTO.getStartTime());
        exam.setEndTime(examAddDTO.getEndTime());
        exam.setStatus(0);

        examMapper.insert(exam);
        return exam.getExamId().toString();
    }


    /**
     * 给指定竞赛添加题目
     *
     * @param addExamQuestionDTO
     * @return
     */
    @Override
    public boolean addExamQuestion(AddExamQuestionDTO addExamQuestionDTO) {
        // 校验数据
        // 校验竞赛是否存在
        Long examId = addExamQuestionDTO.getExamId();
        checkExamIsExist(examId);

        // 校验题目是否存在
        List<Question> questions = questionMapper.selectBatchIds(addExamQuestionDTO.getQuestionIdSet());
        if (CollectionUtils.isEmpty(questions) || questions.size() < addExamQuestionDTO.getQuestionIdSet().size()) {
            throw new SystemServiceException(SystemServiceErrorCode.QUESTION_IS_EMPTY_OR_ERROR);
        }

        // 添加题目
        int index = 0;
        List<ExamQuestion> examQuestions = new ArrayList<>();
        for (Long questionId : addExamQuestionDTO.getQuestionIdSet()) {
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId(examId);
            examQuestion.setQuestionId(questionId);
            examQuestion.setQuestionOrder(index);
            examQuestions.add(examQuestion);
            index++;
        }

        return saveBatch(examQuestions);
    }


    /**
     * 获取竞赛详细信息
     *
     * @param examId
     * @return
     */
    @Override
    public ExamDetailVO getExamDetail(Long examId) {
        // 校验数据
        Exam exam = checkExamIsExist(examId);

        ExamDetailVO examDetailVO = new ExamDetailVO();
        BeanUtils.copyProperties(exam, examDetailVO);

        // 获取竞赛题目
        LambdaQueryWrapper<ExamQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamQuestion::getExamId, examId);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(examQuestions)) {
            return examDetailVO;
        }

        List<Long> questionIds = examQuestions.stream().map(examQuestion -> examQuestion.getQuestionId()).collect(Collectors.toList());

        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();

        // 只要id，title，difficult
        questionWrapper.select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                .in(Question::getQuestionId, questionIds);
        List<Question> questions = questionMapper.selectList(questionWrapper);

        List<QuestionDO> questionResult = questions.stream().map(question -> {
            QuestionDO questionDO = new QuestionDO();
            BeanUtils.copyProperties(question, questionDO);
            return questionDO;
        }).collect(Collectors.toList());

        examDetailVO.setExamQuestionList(questionResult);
        return examDetailVO;
    }


    /**
     * 编辑竞赛详细信息
     *
     * @param editExamDTO
     * @return
     */
    @Override
    public boolean editExamDetail(EditExamDTO editExamDTO) {
        // 校验数据
        Exam exam = checkExamIsExist(editExamDTO.getExamId());
        if (Constants.PUBLISHED.equals(exam.getStatus())) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_HAS_PUBLISH);
        }

        LambdaQueryWrapper<Exam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Exam::getTitle, editExamDTO.getTitle()).ne(Exam::getExamId, editExamDTO.getExamId());
        List<Exam> exams = examMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(exams)) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_TITLE_HAS_EXIST);
        }

        if (editExamDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new SystemServiceException(SystemServiceErrorCode.START_TIME_IS_ERROR);
        }

        if (editExamDTO.getStartTime().isAfter(editExamDTO.getEndTime())) {
            throw new SystemServiceException(SystemServiceErrorCode.END_TIME_IS_ERROR);
        }

        // 更新数据
        Exam newExam = new Exam();
        newExam.setExamId(editExamDTO.getExamId());
        newExam.setTitle(editExamDTO.getTitle());
        newExam.setStartTime(editExamDTO.getStartTime());
        newExam.setEndTime(editExamDTO.getEndTime());

        // 返回结果
        return examMapper.updateById(newExam) > 0;
    }


    /**
     * 删除竞赛的某个题目
     *
     * @param examId
     * @param questionId
     * @return
     */
    @Override
    public boolean deleteQuestion(Long examId, Long questionId) {
        // 校验数据
        Exam exam = checkExamIsExist(examId);
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_IS_STARTED);
        }

        // 删除数据
        LambdaQueryWrapper<ExamQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamQuestion::getExamId, examId).eq(ExamQuestion::getQuestionId, questionId);
        return examQuestionMapper.delete(queryWrapper) > 0;
    }


    /**
     * 删除竞赛
     *
     * @param examId
     * @return
     */
    @Override
    public boolean deleteExam(Long examId) {
        Exam exam = checkExamIsExist(examId);
        if (Constants.PUBLISHED.equals(exam.getStatus())) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_HAS_PUBLISH);
        }

        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_IS_STARTED);
        }

        LambdaQueryWrapper<ExamQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamQuestion::getExamId, examId);
        examQuestionMapper.delete(queryWrapper);
        examCacheManager.deleteCache(examId);

        return examMapper.deleteById(examId) > 0;
    }


    /**
     * 发布竞赛
     *
     * @param examId
     * @return
     */
    @Override
    public boolean publishExam(Long examId) {
        // 校验数据
        Exam exam = checkExamIsExist(examId);
        if (exam.getEndTime().isBefore(LocalDateTime.now())) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_IS_FINISH);
        }

        LambdaQueryWrapper<ExamQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamQuestion::getExamId, examId);
        Long number = examQuestionMapper.selectCount(queryWrapper);
        if (number == null || number <= 0) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_NOT_HAS_QUESTION);
        }

        // 将竞赛存储到redis中
        examCacheManager.addCache(exam);

        // 更新status
        exam.setStatus(ExamEnum.PUBLISHED.getCode());
        return examMapper.updateById(exam) > 0;
    }


    /**
     * 撤销发布
     *
     * @param examId
     * @return
     */
    @Override
    public boolean cancelPublishExam(Long examId) {
        // 校验数据
        Exam exam = checkExamIsExist(examId);
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new SystemServiceException(SystemServiceErrorCode.EXAM_IS_STARTED);
        }

        // 将竞赛信息从redis中移除
        examCacheManager.deleteCache(examId);

        // 修改竞赛状态
        exam.setStatus(ExamEnum.UNPUBLISHED.getCode());
        return examMapper.updateById(exam) > 0;
    }


}
