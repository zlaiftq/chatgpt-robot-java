package cn.leo.chatgptrobot.config;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AppConfig.
 *
 * @author zhanglei.
 * @date 2023/5/1 11:23.
 * @description 应用配置.
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    /**
     * 实例化OkHttpClient
     *
     * @return OkHttpClient
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

}
