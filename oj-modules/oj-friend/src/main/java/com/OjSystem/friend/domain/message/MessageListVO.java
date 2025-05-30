package com.OjSystem.friend.domain.message;

import lombok.Data;

import java.util.List;

@Data
public class MessageListVO<T> {
    public long total;
    public List<T> dataList;
}
