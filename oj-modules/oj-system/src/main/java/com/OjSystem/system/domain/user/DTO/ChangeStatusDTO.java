package com.OjSystem.system.domain.user.DTO;

import lombok.Data;

@Data
public class ChangeStatusDTO {
    private Long userId;

    private Integer status;
}
