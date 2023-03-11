package com.zwh.gulisearch;

import com.alibaba.fastjson.JSON;
import com.zwh.gulisearch.config.GuliElasticsearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class GuliSearchApplicationTests {

    @Autowired
    RestHighLevelClient client;

    /**
     * 测试存储数据
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest users = new IndexRequest("users");
        users.id("1");//数据的id
//        users.source("name","zwh","age",20,"gender","男");
        User user = new User();
        user.setUsername("zwh");
        user.setAge(20);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        users.source(jsonString, XContentType.JSON);
        //执行操作
        IndexResponse index = client.index(users, GuliElasticsearchConfig.COMMON_OPTIONS);
        //打印数据
        System.out.println(index);
    }

    @Data
    class User{
        private String username;
        private Integer age;
        private String gender;
    }

    @Test
    void contextLoads() {
        System.out.println(client);
    }

}
