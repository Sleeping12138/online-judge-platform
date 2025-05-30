package com.OjSystem.system.mybatistest;

import com.OjSystem.system.domain.question.Question;
import com.OjSystem.system.mapper.questionmapper.QuestionMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MybatisTest {
    @Autowired
    private QuestionMapper questionMapper;

    @Test
    public void test(){
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", "两数之和");
        List<Question> questions = questionMapper.selectList(queryWrapper);
        System.out.println(questions);
    }
}
