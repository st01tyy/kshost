package edu.bistu.kshost.kscore.service.matchsystem;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.service.Service;
import edu.bistu.kshost.model.Subject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchSystem extends Service
{
    private Subject subject;    //科目

    Element[] elements; //桶

    private ExecutorService fixedThreadPool;    //定长线程池

    private ExecutorService adderThreadPool;    //不定长线程池

    private Matcher matcher;

    private Maintainer maintainer;

    private final int length = 1000;

    public MatchSystem(Subject subject)
    {
        this.subject = subject;
    }

    @Override
    public void run()
    {
        Log.d(this.getClass().getName(), "科目：【" + subject.getName() + "】的匹配系统已启动");

        elements = new Element[length];

        //创建线程池
        fixedThreadPool = Executors.newFixedThreadPool(2, r ->
        {
            Thread thread = new Thread(r);
            thread.setName("match-system");
            return thread;
        });
        adderThreadPool = Executors.newCachedThreadPool(r ->
        {
            Thread thread = new Thread(r);
            thread.setName("match-system");
            return thread;
        });

        matcher = new Matcher(this);
        maintainer = new Maintainer(this);

        fixedThreadPool.execute(matcher);
        fixedThreadPool.execute(maintainer);

        while(!isShutdown())
        {

        }

        matcher.shutdown();
        maintainer.shutdown();

        fixedThreadPool.shutdown();
        adderThreadPool.shutdown();
    }

    public void addUser(User user)
    {
        adderThreadPool.execute(new Adder(user, this));
    }
}
