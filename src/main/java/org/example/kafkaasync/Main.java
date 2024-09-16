package org.example.kafkaasync;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("Starting async network client demo");

        // 创建模拟网络客户端实例
        MockNetworkClient client = new MockNetworkClient();
        logger.info("MockNetworkClient created");

        // 发送异步请求，并定义请求处理逻辑
        logger.info("Sending async request: Hello, Kafka!");
        RequestFuture<String> future = client.send("Hello, Kafka!", () -> {
            logger.info("Processing request in RequestProcessor");
            return "Response: Hello, Client!";
        });

        // 添加请求结果监听器
        logger.info("Adding listener to RequestFuture");
        future.addListener(new RequestFutureListener<String>() {
            @Override
            public void onSuccess(String value) {
                logger.info("Listener: Request succeeded");
                logger.info("Received: {}", value);
            }

            @Override
            public void onFailure(RuntimeException e) {
                logger.error("Listener: Request failed");
                logger.error("Error: {}", e.getMessage());
            }
        });

        // 等待请求结果（最多等待2秒）
        logger.info("Waiting for request result (max 2 seconds)");
        if (future.await(2, TimeUnit.SECONDS)) {
            logger.info("Request completed within timeout");
            if (future.isDone()) {
                try {
                    // 尝试获取请求结果
                    String result = future.value();
                    logger.info("Final result: {}", result);
                } catch (RuntimeException e) {
                    // 处理请求过程中可能发生的异常
                    logger.error("Error occurred while getting result: {}", e.getMessage());
                }
            } else {
                logger.warn("Request is not done, but await returned true");
            }
        } else {
            // 请求超时处理
            logger.warn("Request timed out");
        }

        // 关闭客户端，释放资源
        logger.info("Closing client");
        client.close();

        logger.info("Async network client demo finished");
    }
}