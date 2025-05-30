package com.OjSystem.system.domain.user.VO;

import lombok.Data;

import java.util.List;

@Data
public class UserListVO<T> {
    public long total;
    public List<T> dataList;
}
