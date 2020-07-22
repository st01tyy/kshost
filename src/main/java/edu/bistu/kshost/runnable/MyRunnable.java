package edu.bistu.kshost.runnable;

import edu.bistu.kshost.model.ClientMessage;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class MyRunnable implements Runnable
{
    private boolean shutdown;
    private final ReentrantReadWriteLock shutdownLock;

    protected LinkedBlockingQueue<ClientMessage> messageQueue;

    public MyRunnable()
    {
        shutdown = false;
        shutdownLock = new ReentrantReadWriteLock();
        messageQueue = new LinkedBlockingQueue<>();
    }

    protected boolean isShutdown()
    {
        shutdownLock.readLock().lock();
        boolean res = shutdown;
        shutdownLock.readLock().unlock();
        return res;
    }

    public void setShutdown(boolean shutdown)
    {
        shutdownLock.writeLock().lock();
        this.shutdown = shutdown;
        shutdownLock.writeLock().unlock();
    }

    public void addMessage(ClientMessage message)
    {
        messageQueue.add(message);
    }
}
