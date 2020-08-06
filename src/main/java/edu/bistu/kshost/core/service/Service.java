package edu.bistu.kshost.core.service;

import edu.bistu.kshost.core.model.ClientMessage;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class Service implements Runnable
{
    protected LinkedBlockingQueue<ClientMessage> messageQueue;

    private Boolean isShutdown;   //终止信号
    protected final ReentrantReadWriteLock shutdownLock;    //同步锁

    protected Service()
    {
        messageQueue = new LinkedBlockingQueue<>(); //阻塞队列，线程安全

        isShutdown = false;
        shutdownLock = new ReentrantReadWriteLock();
    }

    protected Boolean isShutdown()
    {
        //检查终止信号
        shutdownLock.readLock().lock();
        Boolean res = isShutdown;
        shutdownLock.readLock().unlock();
        return res;
    }

    public void shutdown()
    {
        //修改终止信号
        shutdownLock.writeLock().lock();
        isShutdown = true;
        shutdownLock.writeLock().unlock();
    }

    public void receiveMessage(ClientMessage message)
    {
        messageQueue.add(message);
    }

}
