package edu.bistu.kshost.kscore.service.matchsystem;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.model.ServerMessage;
import edu.bistu.kshost.kscore.service.Service;
import edu.bistu.kshost.model.Subject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatchSystem extends Service
{
    private Subject subject;    //科目

    Element[] elements; //桶

    private ExecutorService fixedThreadPool;    //定长线程池

    private ExecutorService adderThreadPool;    //不定长线程池

    private Matcher matcher;

    private Maintainer maintainer;

    private final int length = 1000;

    final int size = 2;

    private Set<Long> cancelSet;

    public MatchSystem(Subject subject)
    {
        this.subject = subject;
    }

    @Override
    public void run()
    {
        Log.d(this.getClass().getName(), "科目：【" + subject.getName() + "】的匹配系统已启动");

        elements = new Element[length];
        for(int i = 0; i < elements.length; i++)
        {
            elements[i] = new Element(i ,this);
        }

        //创建线程池
        fixedThreadPool = Executors.newFixedThreadPool(2, r ->
        {
            Thread thread = new Thread(r);
            thread.setName("match-system-fixed");
            return thread;
        });
        adderThreadPool = Executors.newCachedThreadPool(r ->
        {
            Thread thread = new Thread(r);
            thread.setName("match-system-adder");
            return thread;
        });
        cancelSet = new CopyOnWriteArraySet<>();

        matcher = new Matcher(this);
        maintainer = new Maintainer(this);

        fixedThreadPool.execute(matcher);
        fixedThreadPool.execute(maintainer);

        while(!isShutdown())
        {
            try
            {
                ServerMessage message = messageQueue.poll(5, TimeUnit.SECONDS);
                if(message != null)
                {
                    Integer type = message.getMessageType();
                    if(type == 1)
                    {
                        cancelSet.remove(message.getStudentID());
                        addMatchRequest(message.getStudentID());
                    }
                    else if(type == 2)
                    {
                        System.out.println("科目：【" + subject.getName() + "】的匹配系统收到"
                                + message.getStudentID() + "取消匹配通知");
                        cancelSet.add(message.getStudentID());
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        matcher.shutdown();
        maintainer.shutdown();

        fixedThreadPool.shutdown();
        adderThreadPool.shutdown();
    }

    public void addMatchRequest(Long studentID)
    {
        adderThreadPool.execute(new Adder(new MatchRequest(studentID, 1200), this));
    }

    public Long getSubjectID()
    {
        return subject.getId();
    }

    public void addBack(List<MatchRequest> list)
    {
        for(MatchRequest matchRequest : list)
        {
            adderThreadPool.execute(new Adder(matchRequest, this));
        }
    }

    public boolean checkCancel(Long id)
    {
        return cancelSet.contains(id);
    }

    public void createGame(List<MatchRequest> list)
    {
        adderThreadPool.execute(new GameCreator(list, this));
    }

}
