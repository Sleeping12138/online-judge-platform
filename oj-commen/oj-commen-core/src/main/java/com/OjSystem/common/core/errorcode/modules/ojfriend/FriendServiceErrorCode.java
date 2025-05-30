package com.OjSystem.common.core.errorcode.modules.ojfriend;

import com.OjSystem.common.core.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum FriendServiceErrorCode implements ErrorCode {
    // --------------------- 用户登录模块 ---------------------//
    LOGIN_PHONE_IS_EMPTY(600,"登录手机号不能为空"),
    PHONE_FORMAT_IS_ERROR(601,"手机号格式错误"),
    SEND_MESSAGE_ERROR(602,"不能频繁获取验证码"),
    PHONE_NUMBER_IS_EMPTY(603,"手机号为空"),
    CODE_IS_EMPTY(604,"短息验证码为空"),
    VERIFICATION_CODE_IS_EMPTY(605,"验证码以失效"),
    VERIFICATION_CODE_IS_ERROR(605,"验证码错误"),
    USER_IS_NOT_EXIST(606,"用户不存在"),
    USER_IS_BANNED(607,"用户被拉黑"),


    // --------------------- 竞赛模块 ---------------------//
    EXAM_IS_NOT_EXIST(700,"竞赛不存在"),
    EXAM_HAS_START(701,"竞赛已经开始"),
    USER_HAS_ENTER_EXAM(702,"用户以报名该竞赛"),


    // --------------------- 头像上传模块 ---------------------//
    UPLOAD_PHOTO_IS_ERROR(800,"上传头像失败"),



    // --------------------- 题目模块 ---------------------//
    THIS_IS_FIRST_QUESTION(900,"这已经是第一题了"),
    THIS_IS_LAST_QUESTION(901,"这已经是第最后一题了"),


    // --------------------- 判题模块 ---------------------//
    MERGE_USER_CODE_AND_MAIN_IS_ERROR(1000,"合并用户代码和main函数失败"),
    FAILED_NOT_SUPPORT_PROGRAM(1001,"当前不支持此语言"),
    RABBIT_MQ_SEND_MESSAGE_IS_ERROR(1002,"rabbitMQ发送消息异常"),


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
