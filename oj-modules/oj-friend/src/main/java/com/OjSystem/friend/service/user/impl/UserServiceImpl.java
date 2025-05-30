package com.OjSystem.friend.service.user.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.core.constants.HttpConstants;
import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.core.utils.ThreadLocalUtil;
import com.OjSystem.common.message.service.AliMessageService;
import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import com.OjSystem.common.security.Enum.LoginUserEnum;
import com.OjSystem.common.security.domain.LoginUser;
import com.OjSystem.common.security.service.TokenService;
import com.OjSystem.friend.Enum.user.UserStatus;
import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.VO.ExamListVO;
import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.OjSystem.friend.domain.user.DTO.HeadImageUpdateDTO;
import com.OjSystem.friend.domain.user.DTO.UserMessageDTO;
import com.OjSystem.friend.domain.user.DTO.UserUpdateDTO;
import com.OjSystem.friend.domain.user.User;
import com.OjSystem.friend.domain.user.VO.LoginUserVO;
import com.OjSystem.friend.domain.user.VO.UserVO;
import com.OjSystem.friend.mapper.user.UserMapper;
import com.OjSystem.friend.service.user.IUserService;
import com.OjSystem.manager.UserCacheManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private AliMessageService aliMessageService;

    @Autowired
    private RedisService redisService;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserCacheManager userCacheManager;

    @Value("${file.oss.downloadUrl}")
    private String downloadUrl;


    /**
     * 发送短信验证码
     *
     * @param userMessageDTO
     * @return
     */
    @Override
    public boolean sendMessage(UserMessageDTO userMessageDTO) {
        // 校验数据
        if (!StringUtils.hasText(userMessageDTO.getPhone())) {
            throw new FriendServiceException(FriendServiceErrorCode.LOGIN_PHONE_IS_EMPTY);
        }
        if (!checkPhone(userMessageDTO.getPhone())) {
            throw new FriendServiceException(FriendServiceErrorCode.PHONE_FORMAT_IS_ERROR);
        }
        Long remainTime = redisService.getExpire(RedisConstants.PHONE_CODE_KEY + userMessageDTO.getPhone(), TimeUnit.MINUTES);
        if (remainTime != -2L && (phoneCodeExpiration * 60 - remainTime) < 60) {
            throw new FriendServiceException(FriendServiceErrorCode.SEND_MESSAGE_ERROR);
        }

        // 生成随机验证码
        String code = RandomUtil.randomNumbers(6);
        System.out.println(phoneCodeExpiration);

        // 存储到redis中
        redisService.setCacheObject(RedisConstants.PHONE_CODE_KEY + userMessageDTO.getPhone(), code, phoneCodeExpiration, TimeUnit.MINUTES);

        // 发送短信验证码
        return aliMessageService.sendMobileCode(userMessageDTO.getPhone(), code);
    }


    /**
     * 校验手机号格式
     *
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }


    /**
     * 用户登录
     *
     * @param userMessageDTO
     * @return
     */
    @Override
    public String login(UserMessageDTO userMessageDTO) {
        // 校验基本数据
        if (!StringUtils.hasText(userMessageDTO.getPhone())) {
            throw new FriendServiceException(FriendServiceErrorCode.PHONE_NUMBER_IS_EMPTY);
        }
        if (!StringUtils.hasText(userMessageDTO.getCode())) {
            throw new FriendServiceException(FriendServiceErrorCode.CODE_IS_EMPTY);
        }

        // 校验验证码
        String verificationCode = redisService.getCacheObject(RedisConstants.PHONE_CODE_KEY + userMessageDTO.getPhone(), String.class);
        if (!StringUtils.hasText(verificationCode)) {
            throw new FriendServiceException(FriendServiceErrorCode.VERIFICATION_CODE_IS_EMPTY);
        }
        if (!verificationCode.equals(userMessageDTO.getCode())) {
            throw new FriendServiceException(FriendServiceErrorCode.VERIFICATION_CODE_IS_ERROR);
        }

        // 校验成功之后删除redis中存储的验证码
        redisService.deleteObject(RedisConstants.PHONE_CODE_KEY + userMessageDTO.getPhone());


        // 判断是否是新用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, userMessageDTO.getPhone());
        User user = userMapper.selectOne(queryWrapper);

        // 如果是新用户
        if (user == null) {
            user = new User();
            user.setPhone(userMessageDTO.getPhone());
            user.setStatus(UserStatus.NORMAL.getCode());
            userMapper.insert(user);
        }

        // 返回token
        return tokenService.createToken(user.getUserId(), LoginUserEnum.NORMAL.getCode(), user.getNickName(), user.getHeadImage());
    }


    /**
     * 退出登录
     *
     * @param token
     * @return
     */
    @Override
    public boolean logout(String token) {
        return tokenService.deleteLoginUserToken(token);
    }


    /**
     * 获得登录者的信息
     *
     * @param token
     * @return
     */
    @Override
    public LoginUserVO getLoginUserInfo(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        LoginUser loginUser = tokenService.getLoginUserInfoFromToken(token);
        if (loginUser == null) {
            return null;
        }

        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        if (!StrUtil.isEmpty(loginUser.getHeadImage())) {
            loginUserVO.setHeadImage(downloadUrl + loginUser.getHeadImage());
        }

        return loginUserVO;
    }


    /**
     * 获取用户详情信息
     *
     * @return
     */
    @Override
    public UserVO getUserDetailInfo() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_IS_NOT_EXIST);
        }

        UserVO userVO = userCacheManager.getUserById(userId);
        if (userVO == null) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_IS_NOT_EXIST);
        }

        if (StrUtil.isNotEmpty(userVO.getHeadImage())) {
            userVO.setHeadImage(downloadUrl + userVO.getHeadImage());
        }
        return userVO;
    }


    /**
     * 更新用户信息数据
     *
     * @param userUpdateDTO
     * @return
     */
    @Override
    public boolean editUserInfo(UserUpdateDTO userUpdateDTO) {
        // 判断用户是否存在
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_IS_NOT_EXIST);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_IS_NOT_EXIST);
        }

        // 更新数据
        user.setNickName(userUpdateDTO.getNickName());
        user.setSex(userUpdateDTO.getSex());
        user.setSchoolName(userUpdateDTO.getSchoolName());
        user.setMajorName(userUpdateDTO.getMajorName());
        user.setPhone(userUpdateDTO.getPhone());
        user.setEmail(userUpdateDTO.getEmail());
        user.setWechat(userUpdateDTO.getWechat());
        user.setIntroduce(userUpdateDTO.getIntroduce());

        // 更新两个缓存
        userCacheManager.refreshUser(user);

        tokenService.refreshLoginUser(user.getNickName(), user.getHeadImage(), ThreadLocalUtil.get(Constants.USER_KEY, String.class));

        // 保存数据
        return userMapper.updateById(user) > 0;
    }


    /**
     * 更新数据库中关于头像字段
     *
     * @param headImageUpdateDTO
     * @return
     */
    @Override
    public boolean headImageUpdate(HeadImageUpdateDTO headImageUpdateDTO) {
        // 判断用户是否存在
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_IS_NOT_EXIST);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new FriendServiceException(FriendServiceErrorCode.USER_IS_NOT_EXIST);
        }

        user.setHeadImage(headImageUpdateDTO.getHeadImage());

        // 更新缓存
        userCacheManager.refreshUser(user);

        tokenService.refreshLoginUser(user.getNickName(), user.getHeadImage(), ThreadLocalUtil.get(Constants.USER_KEY, String.class));

        // 更新数据库
        return userMapper.updateById(user) > 0;
    }


}
