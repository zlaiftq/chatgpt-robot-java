package cn.leo.chatgptrobot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ApplicationProperties.
 *
 * @author zhanglei.
 * @date 2023/4/9 14:09.
 * @description 应用配置.
 */
@Component
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    @Getter
    @Setter
    private WxMp wxMp = new WxMp();

    @Getter
    @Setter
    private Chatgpt chatgpt = new Chatgpt();

    @Getter
    @Setter
    public static class WxMp {

        private String appId;

        private String secret;

        private String token;

        private String aesKey;

    }

    @Getter
    @Setter
    public static class Chatgpt {

        private String sk;

        private String url;

    }

}
