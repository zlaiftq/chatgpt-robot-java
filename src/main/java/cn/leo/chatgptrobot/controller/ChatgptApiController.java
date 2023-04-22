package cn.leo.chatgptrobot.controller;

import cn.leo.chatgptrobot.service.ChatgptService;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ChatgptApiController.
 *
 * @author zhanglei.
 * @date 2023/4/9 17:20.
 * @description chatgpt接口控制器.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatgpt-api")
public class ChatgptApiController {

    private final ChatgptService chatgptService;

    /**
     * chatgpt问答接口（简易版）
     *
     * @param reqBody 请求体
     * @return 响应结果
     */
    @PostMapping("/send-reply")
    public ResponseEntity<?> sendReply(@RequestHeader("authorization") String authorization,
                                       @RequestBody JSONObject reqBody) {
        if (StringUtils.isBlank(authorization)) {
            return ResponseEntity.ok("authorization is null");
        }
        if (!"zL9491Fh730oAdryvPAyJ1lVKVyoGOzMaF9vzRAL5V8".equals(authorization)) {
            return ResponseEntity.ok("unauthorized");
        }
        if (reqBody == null) {
            return ResponseEntity.ok("param is null");
        }
        String context = reqBody.getString("context");
        if (StringUtils.isBlank(context)) {
            return ResponseEntity.ok("context is blank");
        }
        String gptSk = reqBody.getString("gptSk");
        if (StringUtils.isBlank(gptSk)) {
            return ResponseEntity.ok("gptSk is blank");
        }
        String result;
        try {
            result = chatgptService.sendReply(context, gptSk);
        } catch (Exception e) {
            log.error("post chatgpt error, reason: {}", ExceptionUtils.getFullStackTrace(e));
            result = "post chatgpt error";
        }
        return ResponseEntity.ok(result);
    }

}
