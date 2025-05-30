package com.OjSystem.system.controller.question;

import com.OjSystem.system.domain.question.DO.QuestionDO;
import com.OjSystem.system.domain.question.DTO.AddQuestionDTO;
import com.OjSystem.system.domain.question.DTO.ChangeQuestionDTO;
import com.OjSystem.system.domain.question.DTO.QuestionDTO;
import com.OjSystem.system.domain.question.Question;
import com.OjSystem.system.domain.question.VO.QuestionDetailVO;
import com.OjSystem.system.domain.question.VO.QuestionVO;
import com.OjSystem.system.service.questionservice.IQuestionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/question")
@RestController
public class QuestionController {

    @Autowired
    private IQuestionService questionService;

    /**
     * 获取题目列表
     *
     * @param questionDTO
     * @return
     */
    @RequestMapping("getQuestionList")
    public QuestionVO<QuestionDO> getQuestionList(QuestionDTO questionDTO) {
        PageHelper.startPage(questionDTO.getPageNum(), questionDTO.getPageSize());
        List<QuestionDO> questionList = questionService.getQuestionList(questionDTO);

        long total = new PageInfo<>(questionList).getTotal();
        QuestionVO<QuestionDO> questionVO = new QuestionVO<>();
        questionVO.setTotal(total);
        questionVO.setDataList(questionList);

        return questionVO;
    }

    /**
     * 添加题目
     *
     * @param addQuestionDTO
     * @return
     */
    @RequestMapping("/addQuestion")
    public boolean addQuestion(@RequestBody AddQuestionDTO addQuestionDTO) {
        return questionService.addQuestion(addQuestionDTO);
    }


    /**
     * 获取指定的题目信息
     *
     * @param questionId
     * @return
     */
    @RequestMapping("/getDetailById")
    public QuestionDetailVO getDetailById(Long questionId) {
        return questionService.getDetailById(questionId);
    }


    /**
     * 修改题目信息
     *
     * @return
     */
    @RequestMapping("changeQuestionInfo")
    public boolean changeQuestionInfo(@RequestBody ChangeQuestionDTO changeQuestionDTO) {
        return questionService.changeQuestionInfo(changeQuestionDTO);
    }


    /**
     * 删除题目
     * @param questionId
     * @return
     */
    @RequestMapping("/deleteQuestion")
    public boolean deleteQuestion(Long questionId){
        return questionService.deleteQuestion(questionId);
    }
}
