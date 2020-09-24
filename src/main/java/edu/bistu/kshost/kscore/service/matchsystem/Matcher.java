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
            for(Element element : master.elements)
            {
                List<MatchRequest> list = element.match();
                if(list.size() != master.size)
                    master.addBack(list);
                else
                    master.createGame(list);
            }
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
