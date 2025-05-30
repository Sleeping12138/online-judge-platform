package com.OjSystem.service.impl;

import com.OjSystem.Enum.CodeRunStatus;
import com.OjSystem.api.domain.DTO.JudgeSubmitDTO;
import com.OjSystem.api.domain.VO.QuestionJudgeVO;
import com.OjSystem.api.domain.VO.QuestionResultVO;
import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.constants.JudgeConstants;
import com.OjSystem.domain.SandBoxExecuteResult;
import com.OjSystem.domain.UserSubmit;
import com.OjSystem.mapper.UserSubmitMapper;
import com.OjSystem.service.IJudgeService;
import com.OjSystem.service.ISandboxPoolService;
import com.OjSystem.service.ISandboxService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeServiceImpl implements IJudgeService {

    @Autowired
    private ISandboxService sandboxService;

    @Autowired
    private ISandboxPoolService sandboxPoolService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    /**
     * 判断题目正确性
     *
     * @param judgeSubmitDTO
     * @return
     */
    @Override
    public QuestionJudgeVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO) {
        // 对代码进行编译 + 运行
        SandBoxExecuteResult sandBoxExecuteResult =
                sandboxPoolService.exeJavaCode(judgeSubmitDTO.getUserId(), judgeSubmitDTO.getUserCode(), judgeSubmitDTO.getInputList());

        QuestionJudgeVO questionJudgeVO = new QuestionJudgeVO();

        if (sandBoxExecuteResult != null && CodeRunStatus.SUCCEED.equals(sandBoxExecuteResult.getRunStatus())) {
            // 编译运行成功后对结果的判断
            questionJudgeVO = getQuestionJudgeVOOfAchieve(judgeSubmitDTO, sandBoxExecuteResult, questionJudgeVO);
        } else {
            questionJudgeVO.setPass(Constants.FALSE);
            if (sandBoxExecuteResult == null) {
                questionJudgeVO.setExeMessage(CodeRunStatus.UNKNOWN_FAILED.getMsg());
            } else {
                questionJudgeVO.setExeMessage(sandBoxExecuteResult.getExeMessage());
            }
            questionJudgeVO.setUserExeResultList(null);
            questionJudgeVO.setScore(JudgeConstants.ERROR_SCORE);
        }

        // 保存结果数据到数据库
        UserSubmit userSubmit = new UserSubmit();
        userSubmit.setUserId(judgeSubmitDTO.getUserId());
        userSubmit.setQuestionId(judgeSubmitDTO.getQuestionId());
        userSubmit.setExamId(judgeSubmitDTO.getExamId());
        userSubmit.setProgramType(judgeSubmitDTO.getProgramType());
        userSubmit.setUserCode(judgeSubmitDTO.getUserCode());
        userSubmit.setPass(questionJudgeVO.getPass());
        userSubmit.setScore(judgeSubmitDTO.getDifficulty() * JudgeConstants.DEFAULT_SCORE);
        userSubmit.setExeMessage(questionJudgeVO.getExeMessage());
        userSubmit.setCaseJudgeRes(JSON.toJSONString(questionJudgeVO.getUserExeResultList()));
        userSubmit.setCreateBy(judgeSubmitDTO.getUserId());

        userSubmitMapper.delete(new LambdaQueryWrapper<UserSubmit>()
                .eq(UserSubmit::getUserId, judgeSubmitDTO.getUserId())
                .eq(UserSubmit::getQuestionId, judgeSubmitDTO.getQuestionId())
                .isNull(judgeSubmitDTO.getExamId() == null, UserSubmit::getExamId)
                .eq(judgeSubmitDTO.getExamId() != null, UserSubmit::getExamId, judgeSubmitDTO.getExamId()));
        userSubmitMapper.insert(userSubmit);
        return questionJudgeVO;
    }


    /**
     * 编译运行成功后对结果的判断
     *
     * @param judgeSubmitDTO
     * @param sandBoxExecuteResult
     * @param questionJudgeVO
     * @return
     */
    private static QuestionJudgeVO getQuestionJudgeVOOfAchieve(JudgeSubmitDTO judgeSubmitDTO, SandBoxExecuteResult sandBoxExecuteResult, QuestionJudgeVO questionJudgeVO) {
        List<String> inputList = judgeSubmitDTO.getInputList();
        List<String> outputList = judgeSubmitDTO.getOutputList();
        List<String> sandOutputList = sandBoxExecuteResult.getOutputList();

        if (outputList.size() != sandOutputList.size()) {
            questionJudgeVO.setScore(JudgeConstants.ERROR_SCORE);
            questionJudgeVO.setPass(Constants.FALSE);
            questionJudgeVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return questionJudgeVO;
        }

        List<QuestionResultVO> userExeResultList = new ArrayList<>();
        boolean flag = true;
        for (int i = 0; i < outputList.size(); i++) {
            String input = inputList.get(i);
            String output = outputList.get(i);
            String sandOut = outputList.get(i);
            QuestionResultVO questionResultVO = new QuestionResultVO();
            questionResultVO.setInput(input);
            questionResultVO.setOutput(output);
            questionResultVO.setExeOutput(sandOut);
            userExeResultList.add(questionResultVO);
            if (!output.equals(sandOut)) {
                flag = false;
            }
        }

        questionJudgeVO.setUserExeResultList(userExeResultList);

        if (!flag) {
            questionJudgeVO.setPass(Constants.FALSE);
            questionJudgeVO.setScore(JudgeConstants.ERROR_SCORE);
            questionJudgeVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return questionJudgeVO;
        }

        if (sandBoxExecuteResult.getUseTime() > judgeSubmitDTO.getTimeLimit()) {
            questionJudgeVO.setPass(Constants.FALSE);
            questionJudgeVO.setScore(JudgeConstants.ERROR_SCORE);
            questionJudgeVO.setExeMessage(CodeRunStatus.OUT_OF_TIME.getMsg());
            return questionJudgeVO;
        }

        if (sandBoxExecuteResult.getUseMemory() > judgeSubmitDTO.getSpaceLimit()) {
            questionJudgeVO.setPass(Constants.FALSE);
            questionJudgeVO.setScore(JudgeConstants.ERROR_SCORE);
            questionJudgeVO.setExeMessage(CodeRunStatus.OUT_OF_MEMORY.getMsg());
            return questionJudgeVO;
        }

        questionJudgeVO.setPass(Constants.TRUE);
        int score = judgeSubmitDTO.getDifficulty() * JudgeConstants.DEFAULT_SCORE;
        questionJudgeVO.setScore(score);
        return questionJudgeVO;
    }
}
