package com.OjSystem.system.domain.exam.DTO;

import com.OjSystem.common.core.domain.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamGetListDTO extends PageParam {
    private String title;

    private String startTime;

    private String endTime;
}
