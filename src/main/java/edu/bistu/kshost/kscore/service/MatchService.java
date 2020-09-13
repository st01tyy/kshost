package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.model.ClientMessage;
import edu.bistu.kshost.kscore.service.matchsystem.*;
import edu.bistu.kshost.model.Subject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatchService extends Service
{
    private Map<Long, MatchSystem> matchSystemMap;

    private ExecutorService cachedThreadPool;

    @Override
    public void run()
    {
        Log.d(this.getClass().getName(), "服务启动");

        matchSystemMap = new HashMap<>();

        cachedThreadPool = Executors.newCachedThreadPool(r ->
        {
            Thread thread = new Thread(r);
            thread.setName("match-system");
            return thread;
        });

        //开始循环
        while(!isShutdown())
        {
            try
            {
                ClientMessage message = messageQueue.poll(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Iterator<MatchSystem> iterator = matchSystemMap.values().iterator();
        while(iterator.hasNext())
        {
            MatchSystem matchSystem = iterator.next();
            matchSystem.shutdown();
        }

        cachedThreadPool.shutdown();
        Log.d(this.getClass().getName(), "服务关闭");
    }

    public void addMatchSystem(Subject subject)
    {
        matchSystemMap.put(subject.getId(), new MatchSystem(subject));
    }
}
