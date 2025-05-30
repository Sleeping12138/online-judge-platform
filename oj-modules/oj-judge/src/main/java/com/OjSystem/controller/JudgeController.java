package com.OjSystem.controller;

import com.OjSystem.api.domain.DTO.JudgeSubmitDTO;
import com.OjSystem.api.domain.VO.QuestionJudgeVO;
import com.OjSystem.service.IJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/judge")
@RestController
public class JudgeController {

    @Autowired
    private IJudgeService judgeService;

    /**
     * 判断题目正确性
     * @param judgeSubmitDTO
     * @return
     */
    @RequestMapping("/doJudgeJavaCode")
    public QuestionJudgeVO doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO){
        return judgeService.doJudgeJavaCode(judgeSubmitDTO);
    }
}
