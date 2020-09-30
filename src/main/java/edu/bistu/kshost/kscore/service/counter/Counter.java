package edu.bistu.kshost.kscore.service.counter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Counter implements Runnable
{
    private final CounterTask counterTask;

    private double time;

    private final LinkedBlockingQueue<Byte> queue;

    public Counter(int time, CounterTask counterTask)
    {
        this.time = time;
        this.counterTask = counterTask;
        queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run()
    {
        System.out.println("时间为" + (int) time + "秒的计时器启动");
        while(time > 0)
        {
            try
            {
                counterTask.process();
                Byte val = queue.poll(500, TimeUnit.MILLISECONDS);
                if(val != null)
                    break;
                else
                    time -= 0.5;
            }
            catch (InterruptedException e)
            {
                System.out.println("计时器抛出异常：" + e.getMessage());
                counterTask.onFinish();
            }
        }
        if(time - 0.0 == 0)
        {
            System.out.println("计时器计时结束");
            counterTask.onFinish();
        }
        else
        {
            System.out.println("计时器提前终止");
            counterTask.onStop();
        }
    }

    public void stop()
    {
        queue.add((byte) 1);
    }
}
