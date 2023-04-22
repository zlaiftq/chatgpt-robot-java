package cn.leo.chatgptrobot.controller;

import cn.leo.chatgptrobot.service.ChatgptService;
import cn.leo.chatgptrobot.utils.AsyncBaseQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * WechatCallbackController.
 *
 * @author zhanglei.
 * @date 2023/4/9 13:17.
 * @description 微信回调控制器.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wechat")
public class WechatCallbackController {

    private final WxMpService wxMpService;

    private final ChatgptService chatgptService;

    /**
     * 签名验证
     *
     * @param signature 签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param echostr 随机数字符串
     * @return 验证结果
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        // 验签
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        } else {
            return "非法请求";
        }
    }

    /**
     * 消息接收
     *
     * @param signature 签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param requestBody 请求体
     * @return 消息回复
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody String requestBody) {
        // 验签
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            return "非法请求";
        }

        // 消息处理
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);

        // 返回结果
        return Objects.requireNonNull(syncHandleMessage(inMessage)).toXml();
    }

    /**
     * 同步消息处理
     *
     * @param inMessage 消息内容
     */
    private WxMpXmlOutMessage syncHandleMessage(WxMpXmlMessage inMessage) {
        // 入参
        if (inMessage == null) {
            log.error("async message handle param is null");
            return null;
        }
        WxMpXmlOutTextMessage outMessage = new WxMpXmlOutTextMessage();
        outMessage.setToUserName(inMessage.getFromUser());
        outMessage.setFromUserName(inMessage.getToUser());
        outMessage.setCreateTime(System.currentTimeMillis() / 1000L);
        String result;
        try {
            // chatgpt问答接口
            result = chatgptService.sendReply(inMessage.getContent(), null);
        } catch (Exception e) {
            log.error("sync chatgpt send and reply error，reason：{}", ExceptionUtils.getFullStackTrace(e));
            // 为发送者回复消息
            result = "处理繁忙，请稍后再试 ... ";
        }
        // 为发送者回复消息
        outMessage.setContent(result);
        return outMessage;
    }

    /**
     * 异步消息处理（使用客服接口异步回复，需要公众号进行微信认证）
     *
     * @param inMessage 消息内容
     */
    private void asyncHandleMessage(WxMpXmlMessage inMessage) {
        // 异步执行chatgpt问答接口
        CompletableFuture.supplyAsync(() -> {
            try {
                // 入参
                if (inMessage == null) {
                    log.error("async message handle param is null");
                    throw new RuntimeException("async message handle param is null");
                }
                // chatgpt问答接口
                String content = chatgptService.sendReply(inMessage.getContent(), null);
                // 为发送者回复消息
                wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage.TEXT()
                        .toUser(inMessage.getFromUser())
                        .content(content)
                        .build());
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
    }

    /**
     * 等待消息处理（配合客服接口异步回复使用，同步立即返回）
     *
     * @param inMessage 消息内容
     * @return 消息结果
     */
    private WxMpXmlOutMessage waitHandleMessage(WxMpXmlMessage inMessage) {
        if (inMessage == null) {
            return null;
        }

        String content = "正在处理，请稍等 ... ";

        WxMpXmlOutTextMessage outMessage = new WxMpXmlOutTextMessage();
        outMessage.setToUserName(inMessage.getFromUser());
        outMessage.setFromUserName(inMessage.getToUser());
        outMessage.setCreateTime(System.currentTimeMillis() / 1000L);
        outMessage.setContent(content);

        return outMessage;
    }

}
