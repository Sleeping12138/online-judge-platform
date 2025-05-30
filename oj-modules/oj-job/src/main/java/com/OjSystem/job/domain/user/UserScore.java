package com.OjSystem.job.domain.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserScore {

    private Long examId;

    private Long userId;

    private int score;

    private int examRank;
}
