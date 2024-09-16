package org.example.kafkaasync;

// 请求Future监听器接口，用于异步处理请求结果
public interface RequestFutureListener<T> {
    // 当请求成功完成时调用
    // value: 请求的结果值
    void onSuccess(T value);

    // 当请求失败时调用
    // e: 请求过程中抛出的异常
    void onFailure(RuntimeException e);
}