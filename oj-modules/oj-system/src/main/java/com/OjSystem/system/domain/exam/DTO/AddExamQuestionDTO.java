package com.OjSystem.system.domain.exam.DTO;

import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class AddExamQuestionDTO {
    private Long examId;
    private LinkedHashSet<Long> questionIdSet;
}
