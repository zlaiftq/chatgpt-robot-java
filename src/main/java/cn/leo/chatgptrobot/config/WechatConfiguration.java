package cn.leo.chatgptrobot.config;

import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WechatConfiguration.
 *
 * @author zhanglei.
 * @date 2023/4/9 13:43.
 * @description 微信配置文件.
 */
@Configuration
@RequiredArgsConstructor
public class WechatConfiguration {

    private final ApplicationProperties applicationProperties;


    /**
     * 构建wx服务接口
     *
     * @return wx服务接口
     */
    @Bean
    public WxMpService wxMpService() {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(applicationProperties.getWxMp().getAppId());
        config.setSecret(applicationProperties.getWxMp().getSecret());
        config.setToken(applicationProperties.getWxMp().getToken());
        config.setAesKey(applicationProperties.getWxMp().getAesKey());
        // 构建wx服务接口
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);
        return wxMpService;
    }

}
