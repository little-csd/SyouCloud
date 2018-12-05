package com.example.asus.syoucloud.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private static ThreadPool threadPool;
    private ExecutorService service;

    private ThreadPool() {
        service = Executors.newFixedThreadPool(10);
    }

    public static ThreadPool getInstance() {
        if (threadPool == null) threadPool = new ThreadPool();
        return threadPool;
    }

    public void execute(Runnable r) {
        service.execute(r);
    }
}
