package com.OjSystem.system.domain.exam;

import lombok.Data;

import java.util.List;

@Data
public class ExamListVO<T> {
    public long total;
    public List<T> dataList;
}
