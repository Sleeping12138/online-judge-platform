package com.OjSystem.job.domain.user;

import com.OjSystem.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_user_exam")
public class UserExam extends BaseEntity {

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "USER_EXAM_ID", type = IdType.ASSIGN_ID)
    private Long userExamId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long examId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private Integer score;

    private Integer examRank;
}
