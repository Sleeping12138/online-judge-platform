package com.OjSystem.common.core.errorcode.modules.ojsystem;

import com.OjSystem.common.core.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum SystemServiceErrorCode implements ErrorCode {
    // --------------------- 管理员登录模块 ---------------------//
    SYSUSER_IS_NOT_EXIST(100, "管理员不存在"),
    PASSWORD_IS_ERROR(101, "管理员登录密码错误"),


    // --------------------- 管理员增删模块 ---------------------//
    ADMIN_USER_HAS_EXIST(200, "新增管理员已经存在"),


    // --------------------- 题目模块 ---------------------//
    QUESTION_HAS_EXIST(300, "您添加的题目已经存在"),
    QUESTION_IS_NOT_EXIST(301, "题目不存在"),


    // --------------------- 竞赛模块 ---------------------//
    EXAM_HAS_EXIST(400, "您添加的竞赛题目已经存在"),
    START_TIME_IS_ERROR(401, "开始时间设置错误"),
    END_TIME_IS_ERROR(402, "结束时间设置错误"),
    EXAM_IS_NOT_EXIST(403, "竞赛不存在"),
    QUESTION_IS_EMPTY_OR_ERROR(404, "题目为空或有题目不存在"),
    EXAM_TITLE_HAS_EXIST(405, "竞赛题目已经存在"),
    EXAM_IS_STARTED(406, "竞赛已开始，信息不可修改"),
    EXAM_NOT_HAS_QUESTION(407, "发布的竞赛没有题目"),
    EXAM_IS_FINISH(408,"竞赛已结束"),
    EXAM_HAS_PUBLISH(409,"竞赛已发布，不可进行更改操作"),


    // --------------------- 用户模块 ---------------------//
    USER_IS_NOT_EXIST(500,"用户不存在"),

    ;

    private Integer code;
    private String errorMsg;

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getErrMsg() {
        return this.errorMsg;
    }
}
