package com.OjSystem.friend.service.user.impl;

import cn.hutool.json.JSONUtil;
import com.OjSystem.api.RemoteJudgeService;
import com.OjSystem.api.domain.DTO.JudgeSubmitDTO;
import com.OjSystem.api.domain.VO.QuestionJudgeVO;
import com.OjSystem.api.domain.VO.QuestionResultVO;
import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.core.utils.ThreadLocalUtil;
import com.OjSystem.friend.Enum.user.ProgramType;
import com.OjSystem.friend.Enum.user.QuestionResType;
import com.OjSystem.friend.domain.QuestionCase;
import com.OjSystem.friend.domain.question.Question;
import com.OjSystem.friend.domain.user.DTO.QuestionJudgeDTO;
import com.OjSystem.friend.domain.user.UserSubmit;
import com.OjSystem.friend.mapper.question.QuestionMapper;
import com.OjSystem.friend.mapper.user.QuestionJudgeMapper;
import com.OjSystem.friend.rabbitmq.JudgeProducer;
import com.OjSystem.friend.service.user.IQuestionJudgeService;
import com.alibaba.fastjson2.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionJudgeServiceImpl implements IQuestionJudgeService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

    @Autowired
    private JudgeProducer judgeProducer;

    @Autowired
    private QuestionJudgeMapper questionJudgeMapper;

    /**
     * 提交题目
     *
     * @param questionJudgeDTO
     * @return
     */
    @Override
    public QuestionJudgeVO submit(QuestionJudgeDTO questionJudgeDTO) {
        Integer programType = questionJudgeDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)) {
            JudgeSubmitDTO judgeSubmitDTO = createJudgeSubmitDTO(questionJudgeDTO);
            return remoteJudgeService.doJudgeJavaCode(judgeSubmitDTO);
        }
        throw new FriendServiceException(FriendServiceErrorCode.FAILED_NOT_SUPPORT_PROGRAM);
    }


    /**
     * 提交题目（rabbitMQ）
     *
     * @param questionJudgeDTO
     * @return
     */
    @Override
    public boolean submitRabbitMQ(QuestionJudgeDTO questionJudgeDTO) {
        Integer programType = questionJudgeDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)) {
            JudgeSubmitDTO judgeSubmitDTO = createJudgeSubmitDTO(questionJudgeDTO);
            judgeProducer.sendMessageToRabbitMQ(judgeSubmitDTO);
            return true;
        }
        throw new FriendServiceException(FriendServiceErrorCode.FAILED_NOT_SUPPORT_PROGRAM);
    }


    /**
     * 创建JudgeSubmitDTO对象
     *
     * @param questionJudgeDTO
     * @return
     */
    private JudgeSubmitDTO createJudgeSubmitDTO(QuestionJudgeDTO questionJudgeDTO) {
        Long questionId = questionJudgeDTO.getQuestionId();
        Question question = questionMapper.selectById(questionId);

        JudgeSubmitDTO judgeSubmitDTO = new JudgeSubmitDTO();

        BeanUtils.copyProperties(question, judgeSubmitDTO);

        judgeSubmitDTO.setUserId(ThreadLocalUtil.get(Constants.USER_ID, Long.class));
        judgeSubmitDTO.setExamId(questionJudgeDTO.getExamId());
        judgeSubmitDTO.setProgramType(questionJudgeDTO.getProgramType());
        judgeSubmitDTO.setUserCode(codeConnect(questionJudgeDTO.getUserCode(), question.getMainFuc()));

        List<QuestionCase> questionCaseList = JSONUtil.toList(question.getQuestionCase(), QuestionCase.class);

        List<String> inputList = questionCaseList.stream().map(QuestionCase::getInput).collect(Collectors.toList());
        judgeSubmitDTO.setInputList(inputList);

        List<String> outputList = questionCaseList.stream().map(QuestionCase::getOutput).collect(Collectors.toList());
        judgeSubmitDTO.setOutputList(outputList);

        return judgeSubmitDTO;
    }


    /**
     * 合并用户代码和main函数
     *
     * @param userCode
     * @param mainFunc
     * @return
     */
    private String codeConnect(String userCode, String mainFunc) {
        String targetCharacter = "}";
        int targetLastIndex = userCode.lastIndexOf(targetCharacter);
        if (targetLastIndex != -1) {
            return userCode.substring(0, targetLastIndex) + "\n" + mainFunc + "\n" + userCode.substring(targetLastIndex);
        }
        throw new FriendServiceException(FriendServiceErrorCode.MERGE_USER_CODE_AND_MAIN_IS_ERROR);
    }


    /**
     * 获取判题结果
     *
     * @param examId
     * @param questionId
     * @param currentTime
     * @return
     */
    @Override
    public QuestionJudgeVO getExeResult(Long examId, Long questionId, String currentTime) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        UserSubmit userSubmit = questionJudgeMapper.selectCurrentUserSubmit(userId, examId, questionId, currentTime);
        QuestionJudgeVO questionJudgeVO = new QuestionJudgeVO();

        if (userSubmit == null) {
            questionJudgeVO.setPass(QuestionResType.IN_JUDGE.getValue());
        } else {
            questionJudgeVO.setPass(userSubmit.getPass());
            questionJudgeVO.setExeMessage(userSubmit.getExeMessage());
            questionJudgeVO.setUserExeResultList(JSON.parseArray(userSubmit.getCaseJudgeRes(), QuestionResultVO.class));
        }
        return questionJudgeVO;
    }
}
