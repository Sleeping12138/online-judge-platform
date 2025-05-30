package com.OjSystem.job.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import com.OjSystem.job.domain.exam.Exam;
import com.OjSystem.job.domain.exam.RankInfo;
import com.OjSystem.job.domain.message.Message;
import com.OjSystem.job.domain.message.MessageText;
import com.OjSystem.job.domain.message.VO.MessageTextVO;
import com.OjSystem.job.domain.user.UserScore;
import com.OjSystem.job.mapper.exam.ExamMapper;
import com.OjSystem.job.mapper.user.UserExamMapper;
import com.OjSystem.job.mapper.user.UserSubmitMapper;
import com.OjSystem.job.service.IMessageService;
import com.OjSystem.job.service.IMessageTextService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExamXxlJob {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IMessageTextService messageTextService;

    @Autowired
    private UserExamMapper userExamMapper;

    /**
     * 刷新竞赛是否完成
     */
    @XxlJob("examListOrganizeHandler")
    public void examListOrganizeHandler() {
        log.info("examListOrganizeHandler执行成功！！！");

        //未完赛竞赛列表
        List<Exam> unFinishList = examMapper.selectList(new
                LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime,
                        Exam::getEndTime)
                .gt(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, 1)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(unFinishList, RedisConstants.EXAM_UNFINISHED_LIST);

        //历史竞赛列表
        List<Exam> historyExamList = examMapper.selectList(new
                LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime,
                        Exam::getEndTime)
                .le(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, 1)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(historyExamList, RedisConstants.EXAM_HISTORY_LIST);
    }

    public void refreshCache(List<Exam> examList, String examListKey) {
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }
        Map<String, Exam> examMap = new HashMap<>();
        List<Long> examIdList = new ArrayList<>();
        for (Exam exam : examList) {
            examMap.put(getDetailKey(exam.getExamId()), exam);
            examIdList.add(exam.getExamId());
        }
        redisService.multiSet(examMap); //刷新详情缓存
        redisService.deleteObject(examListKey);
        redisService.rightPushAll(examListKey, examIdList); //刷新列表缓存
    }


    /**
     * 获取竞赛详细信息key
     *
     * @param examId
     * @return
     */
    private String getDetailKey(Long examId) {
        return RedisConstants.EXAM_DETAIL + examId;
    }


    /**
     * 获取用户消息key
     *
     * @param messageId
     * @return
     */
    private String getMessageKey(Long messageId) {
        return RedisConstants.USER_MESSAGE_LIST + messageId;
    }


    /**
     * 获取消息key
     *
     * @param textId
     * @return
     */
    private String getMessageTextKey(Long textId) {
        return RedisConstants.MESSAGE_DETAIL + textId;
    }


    /**
     * 获取竞赛排名列表
     * @param examId
     * @return
     */
    private String getRankListKey(Long examId) {
        return RedisConstants.EXAM_RANK_LIST + examId;
    }


    /**
     * 计算前一天结束的竞赛情况，并进行存储到数据库
     */
    @XxlJob("examResultHandler")
    public void examResultHandler() {
        // 获取前一天结束的所有竞赛
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusDateTime = now.minusDays(1);

        LambdaQueryWrapper<Exam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Exam::getExamId, Exam::getTitle)
                .eq(Exam::getStatus, Constants.PUBLISHED)
                .ge(Exam::getEndTime, minusDateTime)
                .le(Exam::getEndTime, now);
        List<Exam> exams = examMapper.selectList(queryWrapper);

        // 总分统计 + 排序
        Set<Long> examIds = exams.stream().map(exam -> exam.getExamId()).collect(Collectors.toSet());
        List<UserScore> userScoreList = userSubmitMapper.selectUserScore(examIds);

        // 获取每个竞赛的排名情况
        Map<Long, List<UserScore>> userScoreMap = userScoreList.stream().collect(Collectors.groupingBy(UserScore::getExamId));

        List<MessageText> messageTextList = new ArrayList<>();
        List<Message> messageList = new ArrayList<>();

        // 存储消息和用户消息
        for (Exam exam : exams) {
            List<UserScore> userScores = userScoreMap.get(exam.getExamId());
            int total = userScores.size();
            int rank = 1;
            for (UserScore userScore : userScores) {

                // 存储消息到列表中
                String msgTitle = exam.getTitle() + "——排名情况";
                String msgContent = "您所参与的竞赛：" + exam.getTitle() + "，本次参与竞赛一共" + total + "人， 您排名第" + rank + "名！";

                userScore.setExamRank(rank);
                rank++;

                MessageText messageText = new MessageText();
                messageText.setMessageTitle(msgTitle);
                messageText.setMessageContent(msgContent);
                messageText.setCreateBy(Constants.SYSTEM_USER_ID);
                messageTextList.add(messageText);

                // 存储用户消息到列表中
                Message message = new Message();
                message.setSendId(Constants.SYSTEM_USER_ID);
                message.setRecId(userScore.getUserId());
                message.setCreateBy(Constants.SYSTEM_USER_ID);
                messageList.add(message);

            }

            // 更新数据库中用户排名数据
            userExamMapper.updateUserRankInfo(userScores);

            // 将竞赛排名数据存储到redis中
            List<RankInfo> rankInfoList = userScores.stream().map(userScore -> {
                RankInfo rankInfo = new RankInfo();
                BeanUtils.copyProperties(userScore, rankInfo);
                return rankInfo;
            }).collect(Collectors.toList());
            redisService.rightPushAll(getRankListKey(exam.getExamId()),rankInfoList);
        }

        // 用于存储刷新缓存消息的map
        Map<String, MessageTextVO> messageTextVOMap = new HashMap<>();

        // 将消息id填入
        for (int i = 0; i < messageTextList.size(); i++) {
            MessageText messageText = messageTextList.get(i);

            MessageTextVO messageTextVO = new MessageTextVO();
            BeanUtils.copyProperties(messageText, messageTextVO);
            messageTextVOMap.put(getMessageTextKey(messageText.getTextId()), messageTextVO);


            Message message = messageList.get(i);
            message.setMessageId(messageText.getTextId());
        }

        messageService.batchInsert(messageList);
        messageTextService.batchInsert(messageTextList);

        // 刷新用户消息缓存
        Map<Long, List<Message>> userMessageMap = messageList.stream().collect(Collectors.groupingBy(Message::getRecId));
        for (Map.Entry<Long, List<Message>> entry : userMessageMap.entrySet()) {
            Long userId = entry.getKey();
            List<Message> userMessage = entry.getValue();
            List<Long> userMessageIds = userMessage.stream().map(message -> message.getMessageId()).collect(Collectors.toList());
            redisService.rightPushAll(getMessageKey(userId), userMessageIds);
        }


        // 刷新消息缓存
        redisService.multiSet(messageTextVOMap);
    }
}
