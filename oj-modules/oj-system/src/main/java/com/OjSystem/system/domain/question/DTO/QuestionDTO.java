package com.OjSystem.system.domain.question.DTO;

import com.OjSystem.common.core.domain.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionDTO extends PageParam {
    private Integer difficulty;

    private String title;

    private String excludeIdStr;

    private Set<Long> excludeIdSet;
}
