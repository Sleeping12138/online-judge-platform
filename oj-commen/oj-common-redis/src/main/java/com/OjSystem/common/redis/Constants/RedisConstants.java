package com.OjSystem.common.redis.Constants;

public class RedisConstants {

    // ------------- 管理员登录模块 -------------//
    public static final String LOGIN_USER_ID = "userId";
    public static final String LOGIN_USER_KEY = "userKey";
    public static final String KEY_PREFIX = "loginUser:";
    public static final long KEY_TIME_OUT = 720L;
    public static final long REMAIN_TIME = 180L;

    // ------------- 用户登录模块 -------------//
    public static final String PHONE_CODE_KEY = "PHONE_CODE_";

    public final static String EXAM_UNFINISHED_LIST = "EXAM_UNFINISHED_"; // 未完赛竞赛列表

    public final static String EXAM_HISTORY_LIST = "EXAM_HISTORY_";  // 历史竞赛列表

    public final static String EXAM_DETAIL = "EXAM_DETAIL_";    //竞赛详情信息

    public final static String USER_EXAM_LIST = "USER_EXAM_";
    public final static String USER_DETAIL = "USER_DETAIL_";

    public static final String QUESTION_LIST = "QUESTION_LIST_";

    public static final String EXAM_QUESTION_LIST = "EXAM_QUESTION_LIST_";

    public static final String USER_MESSAGE_LIST = "USER_MESSAGE_LIST_";

    public static final String MESSAGE_DETAIL = "MESSAGE_DETAIL_";

    public static final String EXAM_RANK_LIST = "EXAM_RANK_LIST_";
}
