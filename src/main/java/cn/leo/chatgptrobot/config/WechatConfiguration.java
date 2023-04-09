package cn.leo.chatgptrobot.config;

import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        config.setAppId(applicationProperties.getWx().getAppId());
        config.setSecret(applicationProperties.getWx().getSecret());
        config.setToken(applicationProperties.getWx().getToken());
        config.setAesKey(applicationProperties.getWx().getAesKey());
        // 构建wx服务接口
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);
        return wxMpService;
    }

}
