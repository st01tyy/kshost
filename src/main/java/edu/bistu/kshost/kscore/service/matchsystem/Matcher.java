package edu.bistu.kshost.kscore.service.matchsystem;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.service.Service;

import java.util.List;

public class Matcher extends Service
{
    private MatchSystem master;

    public Matcher(MatchSystem master)
    {
        this.master = master;
    }

    @Override
    public void run()
    {
        Log.d(this.getClass().getName(), "Matcher已启动");

        while(!isShutdown())
        {
            long time1 = System.currentTimeMillis();
            for(Element element : master.elements)
            {
                List<User> list = element.match();
                if(list.size() < 6)
                {
                    //Main.addBack(list);
                }
                else
                {
                    //Memory.result.add(list);
                    //Memory.matched += list.size();
                    // System.out.println("已匹配" + list.size());
                }
            }
            long time2 = System.currentTimeMillis();
            //System.out.println("Matcher used " + (time2 - time1) +"ms");
            try
            {
                Thread.sleep(1700);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Log.d(this.getClass().getName(), "Matcher已关闭");
    }
}
