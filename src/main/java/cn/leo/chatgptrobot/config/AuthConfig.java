package cn.leo.chatgptrobot.config;

import cn.leo.chatgptrobot.utils.IpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AuthConfig.
 *
 * @author zhanglei.
 * @date 2023/1/30 18:38.
 * @description 鉴权配置.
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
public class AuthConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器
     *
     * @param registry 拦截器注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                // 获取请求参数
                String ipAddr = IpUtil.getIpAddr(request);
                String requestUri = request.getRequestURI();
                log.info("request = url:{}, ipAddr:{}", requestUri, ipAddr);
                return true;
            }
        });
    }

}
