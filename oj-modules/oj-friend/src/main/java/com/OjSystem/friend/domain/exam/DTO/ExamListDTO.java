package com.OjSystem.friend.domain.exam.DTO;

import com.OjSystem.common.core.domain.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamListDTO extends PageParam {
    private String title;

    private String startTime;

    private String endTime;

    private Integer type; //0 未完善  1 历史竞赛
}
