package com.OjSystem.friend.domain.exam.DTO;

import com.OjSystem.common.core.domain.PageParam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamRankDTO extends PageParam {

    private Long examId;
}
