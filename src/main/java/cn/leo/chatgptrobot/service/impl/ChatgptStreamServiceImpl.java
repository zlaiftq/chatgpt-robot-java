package cn.leo.chatgptrobot.service.impl;

import cn.hutool.http.ContentType;
import cn.leo.chatgptrobot.config.ApplicationProperties;
import cn.leo.chatgptrobot.entity.chat.Message;
import cn.leo.chatgptrobot.listener.CustomEventSourceListener;
import cn.leo.chatgptrobot.service.ChatgptStreamService;
import cn.leo.chatgptrobot.utils.AsyncBaseQueue;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * ChatgptStreamServiceImpl.
 *
 * @author zhanglei.
 * @date 2023/5/1 12:03.
 * @description chatgpt流式接口服务.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatgptStreamServiceImpl implements ChatgptStreamService {

    private final OkHttpClient okHttpClient;

    private final ApplicationProperties applicationProperties;

    /**
     * chatgpt问答接口（流式输出）
     *
     * @param reqBody 消息内容
     * @param gptSk gptSk
     * @return 回复
     */
    @Override
    public SseEmitter sendReply(JSONObject reqBody, String gptSk) throws Exception {
        // uid
        String uid = UUID.randomUUID().toString();
        // 默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
        });
        // 超时回调
        sseEmitter.onTimeout(() -> {
            log.info("[{}]连接超时...................", uid);
        });
        // 异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.info("[{}]连接异常，{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data(Message.builder().content("发生异常请重试！").build())
                                .reconnectTime(3000));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            log.error("创建sse连接失败，失败原因：{}", ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException("创建sse连接失败");
        }

        // 异步执行chatgpt问答接口
        CompletableFuture.supplyAsync(() -> {
            try {
                streamChatCompletion(reqBody, gptSk, new CustomEventSourceListener(sseEmitter));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return "end of execution !!!";
        }, AsyncBaseQueue.SENDER_ASYNC).exceptionally(e -> {
            CompletionException completionException = new CompletionException(e);
            log.error("async chatgpt send and reply error，reason：{}",
                    ExceptionUtils.getFullStackTrace(completionException));
            throw completionException;
        });

        return sseEmitter;
    }

    /**
     * 发起请求
     *
     * @param reqBody 请求参数
     * @param gptSk gptSk
     * @param eventSourceListener 事件源监听器
     */
    public void streamChatCompletion(JSONObject reqBody, String gptSk, EventSourceListener eventSourceListener) {
        if (Objects.isNull(reqBody)) {
            log.error("reqBody不能为空");
            throw new RuntimeException("reqBody不能为空不能为空");
        }
        if (Objects.isNull(eventSourceListener)) {
            log.error("eventSourceListener不能为空");
            throw new RuntimeException("eventSourceListener不能为空");
        }
        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(reqBody);
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + gptSk)
                    .url(applicationProperties.getChatgpt().getUrl())
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .build();
            // 创建事件
            factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求接口异常，异常原因：{}", ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException("请求接口异常");
        }
    }

}
