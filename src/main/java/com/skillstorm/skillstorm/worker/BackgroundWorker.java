package com.skillstorm.skillstorm.worker;

public interface BackgroundWorker<T>{
    void process(T val);
}
