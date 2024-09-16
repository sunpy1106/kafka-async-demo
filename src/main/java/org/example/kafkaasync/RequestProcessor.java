package org.example.kafkaasync;

// 请求处理器接口，定义了如何处理异步请求
public interface RequestProcessor<T> {
    // 处理请求的方法
    // 返回类型 T 允许处理器返回任意类型的结果
    // 可能抛出 RuntimeException，用于表示处理过程中的错误
    T process() throws RuntimeException;
}
