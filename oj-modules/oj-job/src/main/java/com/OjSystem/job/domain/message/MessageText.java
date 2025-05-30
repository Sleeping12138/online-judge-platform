package com.OjSystem.job.domain.message;

import com.OjSystem.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@TableName("tb_message_text")
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageText extends BaseEntity {

    @TableId(value = "TEXT_ID", type = IdType.ASSIGN_ID)
    private Long textId;

    private String messageTitle;

    private String messageContent;
}
