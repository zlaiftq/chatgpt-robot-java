package cn.leo.chatgptrobot.controller;

import cn.leo.chatgptrobot.service.ChatgptStreamService;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ChatgptStreamApiController.
 *
 * @author zhanglei.
 * @date 2023/5/1 12:00.
 * @description chatgpt流式接口控制器.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatgpt-stream-api")
public class ChatgptStreamApiController {

    private final ChatgptStreamService chatgptStreamService;

    /**
     * chatgpt问答接口（流式输出）
     *
     * @param reqBody 请求体
     * @return 响应结果
     */
    @PostMapping("/send-reply")
    public SseEmitter sendReply(@RequestHeader("Authorization") String authorization,
                                @RequestHeader("Gpt-Sk") String gptSk,
                                @RequestBody JSONObject reqBody) {
        if (StringUtils.isBlank(authorization)) {
            throw new RuntimeException("unauthorized");
        }
        if (!"zL9491Fh730oAdryvPAyJ1lVKVyoGOzMaF9vzRAL5V8".equals(authorization)) {
            throw new RuntimeException("unauthorized");
        }
        if (StringUtils.isBlank(gptSk)) {
            throw new RuntimeException("unauthorized");
        }
        SseEmitter sseEmitter;
        try {
            sseEmitter = chatgptStreamService.sendReply(reqBody, gptSk);
        } catch (Exception e) {
            log.error("post chatgpt error, reason: {}", ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException("post chatgpt error");
        }
        return sseEmitter;
    }

}
