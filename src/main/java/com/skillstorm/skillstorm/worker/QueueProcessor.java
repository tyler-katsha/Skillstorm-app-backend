package com.skillstorm.skillstorm.worker;

import jakarta.annotation.PostConstruct;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class QueueProcessor<T> {

    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private final BackgroundWorker<T> worker;

    public QueueProcessor(BackgroundWorker<T> worker) {
        this.worker = worker;
        startProcessingLoop();
    }

    public void enqueue(T val) {
        queue.add(val);
    }

    public void startProcessingLoop(){
        Runnable runnable = () -> {

            while(true) {
                try {
                    T request = queue.take();
                    worker.process(request);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };
        Thread.ofVirtual().start(runnable);
    }
}
