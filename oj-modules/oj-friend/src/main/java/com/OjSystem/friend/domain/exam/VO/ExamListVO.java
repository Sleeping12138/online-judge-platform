package com.OjSystem.friend.domain.exam.VO;

import lombok.Data;

import java.util.List;

@Data
public class ExamListVO<T> {
    public long total;
    public List<T> dataList;
}
