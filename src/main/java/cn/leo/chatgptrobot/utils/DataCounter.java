package cn.leo.chatgptrobot.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DataCounter.
 *
 * @author zhanglei.
 * @date 2023/5/7 18:55.
 * @description 数据计数器.
 */
public class DataCounter {

    /**
     * Caffeine缓存
     */
    private final static Cache<String, AtomicInteger> CACHE = Caffeine.newBuilder()
            // 设置缓存过期时间
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    /**
     * 增加计数并返回计数
     *
     * @param data 数据
     * @return 计数
     */
    public static int increment(String data) {
        AtomicInteger count = CACHE.get(data, k -> new AtomicInteger(0));
        count.incrementAndGet();
        return getCount(data);
    }

    /**
     * 获取计数
     *
     * @param data 数据
     * @return 计数
     */
    public static int getCount(String data) {
        AtomicInteger count = CACHE.getIfPresent(data);
        return count == null ? 0 : count.get();
    }

}
