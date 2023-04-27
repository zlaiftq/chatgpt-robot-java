package cn.leo.chatgptrobot.service;

import com.alibaba.fastjson.JSONObject;

/**
 * ChatgptService.
 *
 * @author zhanglei.
 * @date 2023/4/9 14:25.
 * @description chatgpt接口服务.
 */
public interface ChatgptService {

    /**
     * chatgpt问答接口（不支持上下文）
     *
     * @param message 消息
     * @param gptSk gptSk
     * @return 回复
     */
    String sendReply(String message, String gptSk) throws Exception;

    /**
     * chatgpt问答接口（支持上线文）
     *
     * @param reqBody 消息内容
     * @param gptSk gptSk
     * @return 回复
     */
    String sendReply(JSONObject reqBody, String gptSk) throws Exception;

}
