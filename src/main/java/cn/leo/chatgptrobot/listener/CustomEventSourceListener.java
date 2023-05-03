package cn.leo.chatgptrobot.listener;

import cn.leo.chatgptrobot.entity.chat.ChatCompletionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * CustomEventSourceListener.
 *
 * @author zhanglei.
 * @date 2023/5/1 11:35.
 * @description 自定义事件源监听器.
 */
@Slf4j
public class CustomEventSourceListener extends EventSourceListener {

    private long tokens;

    private final SseEmitter sseEmitter;

    public CustomEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    /**
     * 建立sse连接
     *
     * @param eventSource EventSource
     * @param response    Response
     */
    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        log.info("建立sse连接 ... ");
    }

    /**
     * 监听成功数据
     *
     * @param eventSource EventSource
     * @param id          String
     * @param type        String
     * @param data        String
     */
    @SneakyThrows
    @Override
    public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
        log.info("响应数据：{}", data);
        try {
            Thread.sleep(50); // 延迟1000毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tokens += 1;
        if (data.equals("[DONE]")) {
            log.info("响应数据结束了");
            sseEmitter.send(SseEmitter.event()
                    .id("[TOKENS]")
                    .data("<br/><br/>tokens：" + tokens())
                    .reconnectTime(3000));
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]")
                    .reconnectTime(3000));
            // 传输完成后自动关闭sse
            sseEmitter.complete();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        // 读取Json
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(completionResponse.getId())
                    .data(completionResponse.getChoices().get(0).getDelta())
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("sse信息推送异常，异常原因：{}", ExceptionUtils.getFullStackTrace(e));
            eventSource.cancel();
        }
    }

    /**
     * 关闭sse连接
     *
     * @param eventSource EventSource
     */
    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        log.info("响应数据总共{}tokens", tokens() - 2);
        log.info("关闭sse连接 ... ");
    }

    /**
     * 监听失败数据
     *
     * @param eventSource EventSource
     * @param t           Throwable
     * @param response    Response
     */
    @SneakyThrows
    @Override
    public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
        log.error("sse连接异常，response：{}，error：{}", response, t);
        eventSource.cancel();
    }

    /**
     * 获取响应数据tokens数量
     *
     * @return 响应数据tokens数量
     */
    public long tokens() {
        return tokens;
    }

}
