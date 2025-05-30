package com.OjSystem.system.controller.exam;

import com.OjSystem.system.domain.exam.DTO.AddExamQuestionDTO;
import com.OjSystem.system.domain.exam.DTO.EditExamDTO;
import com.OjSystem.system.domain.exam.DTO.ExamAddDTO;
import com.OjSystem.system.domain.exam.DTO.ExamGetListDTO;
import com.OjSystem.system.domain.exam.ExamListVO;
import com.OjSystem.system.domain.exam.VO.ExamDetailVO;
import com.OjSystem.system.domain.exam.VO.ExamVO;
import com.OjSystem.system.service.examservice.IExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/exam")
@RestController
public class ExamController {

    @Autowired
    private IExamService examService;

    /**
     * 返回竞赛列表
     *
     * @param examGetListDTO
     * @return
     */
    @RequestMapping("/getExamList")
    public ExamListVO<ExamVO> getExamList(ExamGetListDTO examGetListDTO) {
        PageHelper.startPage(examGetListDTO.getPageNum(), examGetListDTO.getPageSize());
        List<ExamVO> examVOList = examService.getExamList(examGetListDTO);

        ExamListVO<ExamVO> examListVO = new ExamListVO<>();
        long total = new PageInfo<>(examVOList).getTotal();

        examListVO.setTotal(total);
        examListVO.setDataList(examVOList);
        return examListVO;
    }

    /**
     * 添加竞赛
     *
     * @param examAddDTO
     * @return
     */
    @RequestMapping("/addExam")
    public String addExam(@RequestBody ExamAddDTO examAddDTO) {
        return examService.addExam(examAddDTO);
    }


    /**
     * 给指定竞赛添加题目
     *
     * @param addExamQuestionDTO
     * @return
     */
    @RequestMapping("/addExamQuestion")
    public boolean addExamQuestion(@RequestBody AddExamQuestionDTO addExamQuestionDTO) {
        return examService.addExamQuestion(addExamQuestionDTO);
    }


    /**
     * 获取竞赛详细信息
     *
     * @param examId
     * @return
     */
    @RequestMapping("/getExamDetail")
    public ExamDetailVO getExamDetail(Long examId) {
        return examService.getExamDetail(examId);
    }


    /**
     * 编辑竞赛详细信息
     *
     * @param editExamDTO
     * @return
     */
    @RequestMapping("/editExamDetail")
    public boolean editExamDetail(@RequestBody EditExamDTO editExamDTO) {
        return examService.editExamDetail(editExamDTO);
    }


    /**
     * 删除竞赛的某个题目
     *
     * @param examId
     * @param questionId
     * @return
     */
    @RequestMapping("/deleteQuestion")
    public boolean deleteQuestion(@RequestParam Long examId, @RequestParam Long questionId) {
        return examService.deleteQuestion(examId, questionId);
    }


    /**
     * 删除竞赛
     *
     * @param examId
     * @return
     */
    @RequestMapping("/deleteExam")
    public boolean deleteExam(Long examId) {
        return examService.deleteExam(examId);
    }


    /**
     * 发布竞赛
     *
     * @param examId
     * @return
     */
    @RequestMapping("/publishExam")
    public boolean publishExam(Long examId) {
        return examService.publishExam(examId);
    }


    /**
     * 撤销发布
     * @param examId
     * @return
     */
    @RequestMapping("/cancelPublishExam")
    public boolean cancelPublishExam(Long examId){
        return examService.cancelPublishExam(examId);
    }
}
