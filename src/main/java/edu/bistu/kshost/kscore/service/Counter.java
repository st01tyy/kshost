package edu.bistu.kshost.kscore.service;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Counter implements Runnable
{
    private Double time;
    private final ReentrantReadWriteLock lock;

    public Counter(Integer time)
    {
        this.time = time.doubleValue();
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public void run()
    {
        while(getTime().compareTo((double) 0) > 0)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            setTime(getTime() - 0.5);
        }
    }

    public Double getTime()
    {
        lock.readLock().lock();
        Double res = time;
        lock.readLock().unlock();
        return res;
    }

    public void setTime(Double time)
    {
        lock.writeLock().lock();
        this.time = time;
        lock.writeLock().unlock();
    }
}
