package com.skillstorm.skillstorm.worker;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
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
            log.info("Starting background processing loop on virtual thread...");
            while(true) {
                try {
                    // .take() blocks until an item is available
                    T request = queue.take();

                    log.info("Dequeued item for processing {}",request);

                    worker.process(request);

                    log.info("Successfully processed item.");
                } catch(InterruptedException e) {
                    log.warn("Background processing loop was interrupted. Shutting down.");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error occurred while processing item in background worker", e);
                }
            }
        };
        Thread.ofVirtual().start(runnable);
    }
}
