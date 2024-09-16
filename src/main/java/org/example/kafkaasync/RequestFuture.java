package org.example.kafkaasync;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// 表示异步请求的结果
public class RequestFuture<T> {
    private static final Logger logger = LogManager.getLogger(RequestFuture.class);

    // 标记请求是否已完成
    private volatile boolean completed = false;
    // 存储请求的结果值
    private T value;
    // 存储请求过程中可能发生的异常
    private RuntimeException exception;
    // 存储所有注册的监听器
    private final List<RequestFutureListener<T>> listeners = new ArrayList<>();
    // 用于实现等待机制的倒计时锁
    private final CountDownLatch latch = new CountDownLatch(1);

    // 检查请求是否已完成
    public boolean isDone() {
        logger.debug("Checking if request is done: {}", completed);
        return completed;
    }

    // 获取请求的结果值
    public T value() {
        logger.debug("Attempting to get request value");
        if (!isDone())
            throw new IllegalStateException("Future is not done");
        if (exception != null) {
            logger.error("Exception encountered: {}", exception.getMessage());
            throw exception;
        }
        logger.debug("Returning request value: {}", value);
        return value;
    }

    // 获取请求过程中可能发生的异常
    public RuntimeException exception() {
        logger.debug("Attempting to get request exception");
        if (!isDone())
            throw new IllegalStateException("Future is not done");
        return exception;
    }

    // 完成请求，设置结果值
    public void complete(T value) {
        logger.info("Completing request with value: {}", value);
        this.value = value;
        this.completed = true;
        fireSuccess();
        latch.countDown();
    }

    // 标记请求失败，设置异常
    public void raise(RuntimeException e) {
        logger.error("Marking request as failed with exception: {}", e.getMessage());
        this.exception = e;
        this.completed = true;
        fireFailure();
        latch.countDown();
    }

    // 添加监听器
    public void addListener(RequestFutureListener<T> listener) {
        logger.debug("Adding listener to request future");
        listeners.add(listener);
        if (isDone()) {
            if (exception != null) {
                logger.debug("Request already failed, notifying listener immediately");
                listener.onFailure(exception);
            } else {
                logger.debug("Request already completed, notifying listener immediately");
                listener.onSuccess(value);
            }
        }
    }

    // 触发所有成功监听器
    private void fireSuccess() {
        logger.debug("Firing success for all listeners");
        for (RequestFutureListener<T> listener : listeners)
            listener.onSuccess(value);
    }

    // 触发所有失败监听器
    private void fireFailure() {
        logger.debug("Firing failure for all listeners");
        for (RequestFutureListener<T> listener : listeners)
            listener.onFailure(exception);
    }

    // 等待请求完成，带超时机制
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        logger.debug("Awaiting request completion with timeout: {} {}", timeout, unit);
        boolean completed = latch.await(timeout, unit);
        logger.debug("Await completed: {}", completed);
        return completed;
    }
}
