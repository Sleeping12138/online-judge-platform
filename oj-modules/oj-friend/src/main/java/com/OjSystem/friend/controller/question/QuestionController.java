package com.OjSystem.friend.controller.question;

import com.OjSystem.friend.domain.exam.DTO.QuestionListDTO;
import com.OjSystem.friend.domain.exam.VO.QuestionListVO;
import com.OjSystem.friend.domain.exam.VO.QuestionVO;
import com.OjSystem.friend.domain.question.DTO.QuestionDetailVO;
import com.OjSystem.friend.service.quesiton.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/question")
@RestController
public class QuestionController {

    @Autowired
    private IQuestionService questionService;


    /**
     * 获取题目列表
     *
     * @param questionListDTO
     * @return
     */
    @RequestMapping("/semiLogin/getQuestionList")
    public QuestionListVO<QuestionVO> getQuestionList(QuestionListDTO questionListDTO) {
        return questionService.getQuestionList(questionListDTO);
    }


    /**
     * 获取竞赛详细信息
     *
     * @param questionId
     * @return
     */
    @RequestMapping("/getQuestionInfo")
    public QuestionDetailVO getQuestionInfo(Long questionId) {
        return questionService.getQuestionInfo(questionId);
    }


    /**
     * 获取上一题
     * @param questionId
     * @return
     */
    @RequestMapping("preQuestion")
    public String preQuestion(Long questionId) {
        return questionService.preQuestion(questionId);
    }


    /**
     * 获取下一题
     * @param questionId
     * @return
     */
    @RequestMapping("nextQuestion")
    public String nextQuestion(Long questionId) {
        return questionService.nextQuestion(questionId);
    }
}
