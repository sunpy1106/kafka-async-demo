package org.example.kafkaasync;

// 导入必要的 Java 并发包类
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// 模拟网络客户端类，用于模拟异步网络操作
public class MockNetworkClient {
    private static final Logger logger = LogManager.getLogger(MockNetworkClient.class);
    // 创建单线程执行器，用于异步执行任务
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 泛型方法，模拟发送异步请求
    // T: 返回结果的类型
    // request: 请求字符串
    // processor: 请求处理器，定义如何处理请求
    public <T> RequestFuture<T> send(String request, RequestProcessor<T> processor) {
        logger.info("Sending request: {}", request);
        // 创建一个 RequestFuture 对象，用于表示异步操作的结果
        RequestFuture<T> future = new RequestFuture<>();
        logger.debug("Created RequestFuture");
        // 创建一个完成处理器，关联 future 和 processor
        RequestFutureCompletionHandler<T> handler = new RequestFutureCompletionHandler<>(future, processor);
        logger.debug("Created RequestFutureCompletionHandler");

        logger.debug("Submitting async task to executor");
        // 提交异步任务到执行器
        executor.submit(() -> {
            logger.debug("Async task started");
            // 模拟网络延迟
            try {
                logger.debug("Simulating network delay (1 second)");
                Thread.sleep(1000); // 等待1秒
            } catch (InterruptedException e) {
                logger.warn("Sleep interrupted", e);
                // 如果线程被中断，重新设置中断状态
                Thread.currentThread().interrupt();
            }
            logger.debug("Calling completion handler's onComplete method");
            // 调用完成处理器的 onComplete 方法
            handler.onComplete();
            logger.debug("Async task completed");
        });

        logger.debug("Returning future immediately");
        // 立即返回 future 对象，不等待任务完成
        return future;
    }

    // 关闭方法，用于关闭执行器服务
    public void close() {
        logger.info("Closing MockNetworkClient");
        executor.shutdown();
        logger.debug("Executor service shut down");
    }
}
