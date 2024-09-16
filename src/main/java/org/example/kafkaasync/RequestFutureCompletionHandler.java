package org.example.kafkaasync;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// 请求完成处理器类，用于处理异步请求完成时的操作
public class RequestFutureCompletionHandler<T> implements RequestCompletionHandler {
    private static final Logger logger = LogManager.getLogger(RequestFutureCompletionHandler.class);

    // 存储与此处理器关联的 RequestFuture 对象
    private final RequestFuture<T> future;
    // 存储与此处理器关联的 RequestProcessor 对象
    private final RequestProcessor<T> processor;

    // 构造函数，初始化 future 和 processor
    public RequestFutureCompletionHandler(RequestFuture<T> future, RequestProcessor<T> processor) {
        this.future = future;
        this.processor = processor;
        logger.debug("RequestFutureCompletionHandler created");
    }

    // 实现 RequestCompletionHandler 接口的 onComplete 方法
    @Override
    public void onComplete() {
        logger.debug("RequestFutureCompletionHandler.onComplete() called");
        try {
            logger.debug("Calling processor.process()");
            // 调用处理器的 process 方法获取结果
            T result = processor.process();
            logger.debug("Processor returned result: {}", result);
            // 使用结果完成 future
            logger.debug("Completing future with result");
            future.complete(result);
        } catch (RuntimeException e) {
            logger.error("Exception caught in onComplete: {}", e.getMessage());
            // 如果处理过程中抛出异常，则将异常传递给 future
            logger.debug("Raising exception to future");
            future.raise(e);
        }
        logger.debug("RequestFutureCompletionHandler.onComplete() finished");
    }
}
