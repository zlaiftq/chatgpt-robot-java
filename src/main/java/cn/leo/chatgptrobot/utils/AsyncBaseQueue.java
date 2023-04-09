package cn.leo.chatgptrobot.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * AsyncBaseQueue.
 *
 * @author zhanglei.
 * @date 2021/6/1 10:59.
 * @description 线程池统一管理.
 */
@Slf4j
public class AsyncBaseQueue {

	/** 核心线程数 */
	private static final int THREAD_CORE_SIZE = 20;

	/** 最大线程数 */
	private static final int THREAD_MAX_SIZE = 50;

	/** 队列大小 */
	private static final int QUEUE_SIZE = 1000;

	/** 空闲超时时间 */
	private static final long KEEP_ALIVE_TIME = 60L;

	/** 创建线程池 */
	public static final ExecutorService SENDER_ASYNC = new ThreadPoolExecutor(
			// 核心线程数
			THREAD_CORE_SIZE,
			// 最大线程数
			THREAD_MAX_SIZE,
			// 空闲超时时间
			KEEP_ALIVE_TIME,
			// 时间单位
			TimeUnit.SECONDS,
			// 工作队列
			new ArrayBlockingQueue<>(QUEUE_SIZE),
			// 线程工厂
			Thread::new,
			// 拒绝策略
			new ThreadPoolExecutor.CallerRunsPolicy());

	/**
	 * 线程池提交任务方法
	 *
	 * @author zhanglei.
	 * @date 2021/6/1 10:59.
	 * @param runnable 任务
	 */
	public static void submit(Runnable runnable) {
		SENDER_ASYNC.submit(runnable);
	}

	/**
	 * 执行task
	 *
	 * @author zhanglei.
	 * @date 2021/6/1 10:59.
	 * @param tasks 任务
	 * @return List<Future<Integer>>
	 * @throws InterruptedException InterruptedException
	 */
	public static <T> List<Future<T>> invokeAll(List<Callable<T>> tasks) throws InterruptedException {
		return SENDER_ASYNC.invokeAll(tasks);
	}

}
