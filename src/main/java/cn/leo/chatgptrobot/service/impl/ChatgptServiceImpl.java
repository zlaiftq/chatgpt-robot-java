package cn.leo.chatgptrobot.service.impl;

import cn.leo.chatgptrobot.config.ApplicationProperties;
import cn.leo.chatgptrobot.service.ChatgptService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatgptServiceImpl.
 *
 * @author zhanglei.
 * @date 2023/4/9 14:31.
 * @description chatgpt接口服务实现.
 */
@Service
@RequiredArgsConstructor
public class ChatgptServiceImpl implements ChatgptService {

    /**
     * 设置缓存大小
     */
    private static final int CACHE_SIZE = 100;

    /**
     * caffeine实现LRU缓存
     */
    private static final Cache<String, String> CHAT_GPT_CACHE = Caffeine.newBuilder()
            .maximumSize(CACHE_SIZE)
            .build();

    /**
     * 应用配置
     */
    private final ApplicationProperties applicationProperties;

    /**
     * chatgpt问答接口
     *
     * @param message 消息
     * @return 回复
     */
    @Override
    public String sendReply(String message) throws Exception {
        // 尝试从缓存中获取响应
        String response = CHAT_GPT_CACHE.getIfPresent(message);

        // 从缓存中获取响应非空校验
        if (response == null) {
            // ChatGPT API URL
            String url = applicationProperties.getChatgpt().getUrl();
            // ChatGPT API Key
            String apiKey = applicationProperties.getChatgpt().getSk();
            // Request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            List<Map<String, Object>> subList = new ArrayList<>();
            Map<String, Object> sub = new HashMap<>();
            sub.put("role", "user");
            sub.put("content", message);
            subList.add(sub);
            requestBody.put("messages", subList);

            // Convert request body to JSON string
            Gson gson = new Gson();
            String requestBodyJson = gson.toJson(requestBody);

            // Send HTTP POST request to ChatGPT API
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setDoOutput(true);
            con.getOutputStream().write(requestBodyJson.getBytes(StandardCharsets.UTF_8));

            // Read API response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
            in.close();

            JSONObject jsonObject = JSONObject.parseObject(responseBuilder.toString());

            JSONArray choices = jsonObject.getJSONArray("choices");

            List<String> contentList = new ArrayList<>();
            for (Object choice : choices) {
                JSONObject choiceJson = JSONObject.parseObject(choice.toString());
                JSONObject messageResult = choiceJson.getJSONObject("message");
                String content = messageResult.getString("content");
                contentList.add(content);
            }

            response = StringUtils.join(contentList, "^^^^^^");

            // 响应加入缓存
            CHAT_GPT_CACHE.put(message, response);
        }

        // 返回结果
        return response;
    }

}
