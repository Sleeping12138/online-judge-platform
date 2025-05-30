package com.OjSystem.friend.domain.exam;

import lombok.Data;

@Data
public class RankInfo {
    private Long userId;

    private int score;

    private int examRank;
}
