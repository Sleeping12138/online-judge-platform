package com.OjSystem.service;

import com.OjSystem.domain.SandBoxExecuteResult;

import java.util.List;

public interface ISandboxPoolService {
    SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList);

}
