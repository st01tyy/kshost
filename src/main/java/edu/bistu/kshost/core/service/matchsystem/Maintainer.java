package edu.bistu.kshost.core.service.matchsystem;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.core.service.Service;

public class Maintainer extends Service
{
    private MatchSystem master;

    public Maintainer(MatchSystem master)
    {
        this.master = master;
    }

    @Override
    public void run()
    {
        Log.d(this.getClass().getName(), "Maintainer已启动");

        while(!isShutdown())
        {
            for(Element element : master.elements)
            {
                element.update();
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Log.d(this.getClass().getName(), "Maintainer已结束");
    }
}
