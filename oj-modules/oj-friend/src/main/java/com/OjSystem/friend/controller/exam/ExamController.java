package com.OjSystem.friend.controller.exam;

import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.DTO.ExamRankDTO;
import com.OjSystem.friend.domain.exam.RankInfo;
import com.OjSystem.friend.domain.exam.VO.ExamListVO;
import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.OjSystem.friend.service.exam.IExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/exam")
@RestController
public class ExamController {

    @Autowired
    private IExamService examService;


    /**
     * 获取竞赛列表（从数据库获取竞赛列表）
     *
     * @param examListDTO
     * @return
     */
    @RequestMapping("/semiLogin/getExamList")
    public ExamListVO<ExamVO> getExamList(ExamListDTO examListDTO) {
        PageHelper.startPage(examListDTO.getPageNum(), examListDTO.getPageSize());
        List<ExamVO> examVOList = examService.getExamList(examListDTO);

        long total = new PageInfo<>(examVOList).getTotal();

        ExamListVO<ExamVO> examListVO = new ExamListVO<>();
        examListVO.setTotal(total);
        examListVO.setDataList(examVOList);
        return examListVO;
    }


    /**
     * 获取竞赛列表（从redis中获取竞赛列表）
     *
     * @param examListDTO
     * @return
     */
    @RequestMapping("/semiLogin/redis/getExamList")
    public ExamListVO<ExamVO> getExamListFromRedis(ExamListDTO examListDTO) {
        return examService.getExamListFromRedis(examListDTO);
    }


    /**
     * 获取竞赛的第一道题目
     *
     * @param examId
     * @return
     */
    @RequestMapping("/getQuestionOfFirst")
    public String getQuestionOfFirst(Long examId) {
        return examService.getQuestionOfFirst(examId);
    }


    /**
     * 获取竞赛题目上一题
     *
     * @param examId
     * @param questionId
     * @return
     */
    @RequestMapping("preQuestion")
    public String preQuestion(Long examId, Long questionId) {
        return examService.preQuestion(examId, questionId);
    }


    /**
     * 获取竞赛题目下一题
     *
     * @param examId
     * @param questionId
     * @return
     */
    @RequestMapping("nextQuestion")
    public String nextQuestion(Long examId, Long questionId) {
        return examService.nextQuestion(examId, questionId);
    }


    /**
     * 获取竞赛排名数据
     * @param examRankDTO
     * @return
     */
    @RequestMapping("/getExamRankInfo")
    public ExamListVO<RankInfo> getExamRankInfo(ExamRankDTO examRankDTO) {
        return examService.getExamRankInfo(examRankDTO);
    }
}
