package com.example.codecheckdemo.example;

/**
 * Description of this file * * @author Lai Yufang * @version 1.0 * @since 2025/7/21
 */
public class Example2 {
    public void deadLoopDemo() {
        ExecutorServerStats stats = new ExecutorServerStats(1);
        while (stats.getQueueSize() > 0) {
            Thread.yield();
        }
    }

    class ExecutorServerStats {
        public ExecutorServerStats(int queueSize) {
            this.queueSize = queueSize;
        }

        private int queueSize;

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }
    }
}