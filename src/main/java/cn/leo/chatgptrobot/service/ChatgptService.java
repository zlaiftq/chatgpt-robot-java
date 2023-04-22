package cn.leo.chatgptrobot.service;

/**
 * ChatgptService.
 *
 * @author zhanglei.
 * @date 2023/4/9 14:25.
 * @description chatgpt接口服务.
 */
public interface ChatgptService {

    /**
     * chatgpt问答接口
     *
     * @param message 消息
     * @param gptSk gptSk
     * @return 回复
     */
    String sendReply(String message, String gptSk) throws Exception;

}
