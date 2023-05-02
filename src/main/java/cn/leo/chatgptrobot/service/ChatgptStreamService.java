package cn.leo.chatgptrobot.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ChatgptStreamService.
 *
 * @author zhanglei.
 * @date 2023/5/1 12:02.
 * @description chatgpt流式接口服务.
 */
public interface ChatgptStreamService {

    /**
     * chatgpt问答接口（流式输出）
     *
     * @param reqBody 消息内容
     * @param gptSk gptSk
     * @return 回复
     */
    SseEmitter sendReply(JSONObject reqBody, String gptSk) throws Exception;

}
