package edu.bistu.kshost.core.service.matchsystem;

public class Maintainer implements Runnable
{

    @Override
    public void run()
    {
        while(Memory.matched < Memory.target)
        {
            for(Element element : Memory.elements)
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
    }
}
