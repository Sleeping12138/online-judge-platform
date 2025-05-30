package com.OjSystem.api;

import com.OjSystem.api.domain.DTO.JudgeSubmitDTO;
import com.OjSystem.api.domain.VO.QuestionJudgeVO;
import com.OjSystem.common.core.constants.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(contextId = "RemoteJudgeService", value = Constants.JUDGE_SERVICE)
public interface RemoteJudgeService {

    @RequestMapping("/judge/doJudgeJavaCode")
    public QuestionJudgeVO doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO);
}
