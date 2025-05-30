package com.OjSystem.job.domain.message.VO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MessageTextVO {

    private Long textId;

    private String messageTitle;

    private String messageContent;
}
