package com.ticket_pipeline.simple_exchange.task;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_exchange.dao.QueueDao;
import com.ticket_pipeline.simple_exchange.domain.QWork;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class GetQWorkQueue {
    private final Lock lock = new ReentrantLock();
    private final Queue<QWork> qWorks = new ConcurrentLinkedQueue<>();
    private final QueueDao queueDao;

    @Constructor
    public GetQWorkQueue(QueueDao queueDao) {
        this.queueDao = queueDao;
    }

    public QWork getQWork() {
        QWork qWork = qWorks.poll();
        return qWork == null ? fillQueue() : qWork;
    }

    private QWork fillQueue() {
        lock.lock();
        try {
            QWork qWork = qWorks.poll();
            if (qWork != null) {
                return qWork;
            } else {
                qWorks.addAll(queueDao.getTasks());
                return qWorks.poll();
            }
        } finally {
            lock.unlock();
        }
    }
}
