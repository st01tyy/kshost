package edu.bistu.kshost.core.service.matchsystem;

import java.util.List;

public class Matcher implements Runnable
{

    @Override
    public void run()
    {
        while(Memory.matched < Memory.target)
        {
            long time1 = System.currentTimeMillis();
            for(Element element : Memory.elements)
            {
                List<User> list = element.match();
                if(list.size() < 6)
                {
                    //Main.addBack(list);
                }
                else
                {
                    Memory.result.add(list);
                    Memory.matched += list.size();
                    // System.out.println("已匹配" + list.size());
                }
            }
            long time2 = System.currentTimeMillis();
            //System.out.println("Matcher used " + (time2 - time1) +"ms");
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }
}
