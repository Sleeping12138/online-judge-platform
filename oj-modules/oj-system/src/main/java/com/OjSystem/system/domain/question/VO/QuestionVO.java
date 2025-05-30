package com.OjSystem.system.domain.question.VO;

import lombok.Data;

import java.util.List;

@Data
public class QuestionVO<T> {
    public long total;
    public List<T> dataList;
}
