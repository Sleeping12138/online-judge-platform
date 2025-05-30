package com.OjSystem.aspect;


import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.core.utils.ThreadLocalUtil;
import com.OjSystem.friend.Enum.user.UserStatus;
import com.OjSystem.friend.domain.user.VO.UserVO;
import com.OjSystem.manager.UserCacheManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class UserStatusCheckAspect {

    @Autowired
    private UserCacheManager userCacheManager;

    @Before(value = "@annotation(com.OjSystem.aspect.CheckUserStatus)")
    public void before(JoinPoint point) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        UserVO userVo = userCacheManager.getUserById(userId);
        if (userVo == null) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_IS_NOT_EXIST);
        }
        if (Objects.equals(userVo.getStatus(), UserStatus.ERROR.getCode())) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_IS_BANNED);
        }
    }
}
