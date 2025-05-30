package com.OjSystem.friend.controller.user;

import com.OjSystem.api.domain.VO.QuestionJudgeVO;
import com.OjSystem.friend.domain.user.DTO.QuestionJudgeDTO;
import com.OjSystem.friend.service.user.IQuestionJudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/question/judge")
@RestController
@Slf4j
public class QuestionJudgeController {

    @Autowired
    private IQuestionJudgeService questionJudgeService;


    /**
     * 提交题目
     * @param questionJudgeDTO
     * @return
     */
    @RequestMapping("/submit")
    public QuestionJudgeVO submit(@RequestBody QuestionJudgeDTO questionJudgeDTO){
        log.info("questionJudgeDTO,{}",questionJudgeDTO);
        return questionJudgeService.submit(questionJudgeDTO);
    }


    /**
     * 提交题目（rabbitMQ）
     * @param questionJudgeDTO
     * @return
     */
    @RequestMapping("/rabbitmq/submit")
    public boolean submitRabbitMQ(@RequestBody QuestionJudgeDTO questionJudgeDTO){
        log.info("questionJudgeDTO,{}",questionJudgeDTO);
        return questionJudgeService.submitRabbitMQ(questionJudgeDTO);
    }


    /**
     * 获取判题结果
     * @param examId
     * @param questionId
     * @param currentTime
     * @return
     */
    @RequestMapping("/getExeResult")
    public QuestionJudgeVO getExeResult(Long examId, Long questionId, String currentTime) {
        return questionJudgeService.getExeResult(examId,questionId,currentTime);
    }
}
